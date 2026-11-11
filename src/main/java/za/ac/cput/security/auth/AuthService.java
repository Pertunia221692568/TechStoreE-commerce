package za.ac.cput.security.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Login;
import za.ac.cput.domain.Registration;
import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;
import za.ac.cput.dto.auth.AuthResponse;
import za.ac.cput.dto.auth.LoginRequest;
import za.ac.cput.dto.auth.RegisterRequest;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.repository.ICustomerRepository;
import za.ac.cput.repository.ILoginRepository;
import za.ac.cput.repository.IRegistrationRepository;
import za.ac.cput.security.password.PasswordPolicy;
import za.ac.cput.security.token.JwtService;
import za.ac.cput.security.verification.EmailService;
import za.ac.cput.security.verification.VerificationService;
import za.ac.cput.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Central authentication & account-lifecycle service.
 * Controllers stay thin; all security-related business rules live here.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicy passwordPolicy;
    private final VerificationService verificationService;
    private final EmailService emailService;
    private final boolean exposeCodes;
    private final boolean requireEmailVerification;
    private final LoginAttemptService loginAttemptService;
    private final ICustomerRepository customerRepository;
    private final ILoginRepository loginRepository;
    private final IRegistrationRepository registrationRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       PasswordPolicy passwordPolicy,
                       VerificationService verificationService,
                       EmailService emailService,
                       @Value("${app.auth.expose-codes:false}") boolean exposeCodes,
                       @Value("${app.auth.require-email-verification:false}") boolean requireEmailVerification,
                       LoginAttemptService loginAttemptService,
                       ICustomerRepository customerRepository,
                       ILoginRepository loginRepository,
                       IRegistrationRepository registrationRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicy = passwordPolicy;
        this.verificationService = verificationService;
        this.emailService = emailService;
        this.exposeCodes = exposeCodes;
        this.requireEmailVerification = requireEmailVerification;
        this.loginAttemptService = loginAttemptService;
        this.customerRepository = customerRepository;
        this.loginRepository = loginRepository;
        this.registrationRepository = registrationRepository;
    }

    // ─────────────────────────────────────────────────────────────
    // Login
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (loginAttemptService.isLocked(email)) {
            throw new IllegalStateException(
                    "Too many failed login attempts. Please try again in 15 minutes.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (DisabledException ex) {
            throw new IllegalStateException(
                    "Account is not verified. Please enter the verification code sent to your email.");
        } catch (BadCredentialsException ex) {
            loginAttemptService.recordFailure(email);
            throw new BadCredentialsException("Invalid email or password");
        }
        loginAttemptService.recordSuccess(email);

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        userService.updateLastLogin(user.getId());
        recordLogin(user);

        String token = jwtService.generateToken(user);
        Long customerId = resolveCustomerId(user);

        return new AuthResponse(
                token, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getRole(), customerId
        );
    }

    // ─────────────────────────────────────────────────────────────
    // Register (creates DISABLED account + verification code)
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userService.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Enforce strong password policy
        passwordPolicy.enforce(request.getPassword());

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Account starts disabled – must verify email
        User user = userService.createCustomerUser(
                email,
                hashedPassword,
                request.getFirstName().trim(),
                request.getLastName().trim(),
                request.getPhone() != null ? request.getPhone().trim() : null
        );

        Customer customer = CustomerFactory.create(user, request.getAddress());
        customer = customerRepository.save(customer);

        recordRegistration(user);

        if (!requireEmailVerification) {
            // Verification switched off (app.auth.require-email-verification=false):
            // activate the account and log the new customer straight in.
            user.setEnabled(true);
            userService.save(user);
            Map<String, Object> auto = new HashMap<>();
            auto.put("message", "Registration successful. Welcome to TechVault!");
            auto.put("token", jwtService.generateToken(user));
            auto.put("type", "Bearer");
            auto.put("userId", user.getId());
            auto.put("email", user.getEmail());
            auto.put("firstName", user.getFirstName());
            auto.put("lastName", user.getLastName());
            auto.put("role", user.getRole().name());
            auto.put("customerId", customer.getId());
            return auto;
        }

        // Generate verification code
        String verificationCode = verificationService.createEmailVerificationToken(user);
        emailService.sendVerificationCode(user.getEmail(), verificationCode,
                verificationService.emailVerificationMinutes());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful. We have emailed you a verification code.");
        response.put("email", user.getEmail());
        response.put("userId", user.getId());
        response.put("customerId", customer.getId());
        // Only for local demos (app.auth.expose-codes=true). Off by default: the code is emailed only.
        if (exposeCodes) {
            response.put("verificationCode", verificationCode);
            response.put("note", "Demo mode: code exposed because app.auth.expose-codes=true.");
        }

        return response;
    }

    // ─────────────────────────────────────────────────────────────
    // Verify email
    // ─────────────────────────────────────────────────────────────

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public AuthResponse verifyEmail(String email, String code) {
        User user = verificationService.verifyEmail(email, code);

        // Automatically log the user in after successful verification
        userService.updateLastLogin(user.getId());
        recordLogin(user);

        String token = jwtService.generateToken(user);
        Long customerId = resolveCustomerId(user);

        return new AuthResponse(
                token, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getRole(), customerId
        );
    }

    // ─────────────────────────────────────────────────────────────
    // Forgot password
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> forgotPassword(String email) {
        Optional<String> codeOpt = verificationService.createPasswordResetToken(email);

        Map<String, Object> response = new HashMap<>();
        // Always return the same message so we do not leak whether the email exists
        response.put("message",
                "If an account with that email exists, a password reset code has been emailed.");

        codeOpt.ifPresent(code -> {
            emailService.sendPasswordResetCode(email.trim().toLowerCase(), code,
                    verificationService.passwordResetMinutes());
            if (exposeCodes) {
                response.put("resetCode", code);
                response.put("note", "Demo mode: code exposed because app.auth.expose-codes=true.");
            }
        });

        return response;
    }

    // ─────────────────────────────────────────────────────────────
    // Reset password
    // ─────────────────────────────────────────────────────────────

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public Map<String, Object> resetPassword(String email, String code, String newPassword) {
        // Validate strength first
        passwordPolicy.enforce(newPassword);

        User user = verificationService.validatePasswordResetCode(email, code);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userService.save(user);

        verificationService.consumePasswordResetToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password has been reset successfully. You can now log in with your new password.");
        return response;
    }

    // ─────────────────────────────────────────────────────────────
    // Resend verification code
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> resendVerification(String email) {
        Map<String, Object> response = new HashMap<>();
        response.put("message",
                "If that account exists and is not yet verified, a new code has been emailed.");
        userService.findByEmail(email.trim().toLowerCase())
                .filter(u -> !u.isEnabled())
                .ifPresent(u -> {
                    String code = verificationService.createEmailVerificationToken(u);
                    emailService.sendVerificationCode(u.getEmail(), code,
                            verificationService.emailVerificationMinutes());
                    if (exposeCodes) {
                        response.put("verificationCode", code);
                    }
                });
        return response;
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private void recordRegistration(User user) {
        if (user.getRole() != Role.CUSTOMER) return;
        if (registrationRepository.existsByEmail(user.getEmail())) return;
        registrationRepository.save(new Registration(
                user.getEmail(), user.getPasswordHash(), user.getCreatedAt()));
    }

    private void recordLogin(User user) {
        if (user.getRole() == Role.CUSTOMER) recordRegistration(user);
        Login login = loginRepository.findByUsername(user.getEmail())
                .orElseGet(() -> new Login(user.getEmail(), user.getPasswordHash(),
                        user.getCreatedAt(), null));
        login.setLastLoginDate(LocalDateTime.now());
        loginRepository.save(login);
    }

    private Long resolveCustomerId(User user) {
        if (user.getRole() != Role.CUSTOMER) return null;
        return customerRepository.findByUser_Id(user.getId())
                .map(Customer::getId)
                .orElse(null);
    }
}
