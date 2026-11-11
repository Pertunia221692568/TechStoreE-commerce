package za.ac.cput.security.auth;

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
    private final ICustomerRepository customerRepository;
    private final ILoginRepository loginRepository;
    private final IRegistrationRepository registrationRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       PasswordPolicy passwordPolicy,
                       VerificationService verificationService,
                       ICustomerRepository customerRepository,
                       ILoginRepository loginRepository,
                       IRegistrationRepository registrationRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicy = passwordPolicy;
        this.verificationService = verificationService;
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

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (DisabledException ex) {
            throw new IllegalStateException(
                    "Account is not verified. Please enter the verification code sent to your email.");
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

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

        // Generate verification code
        String verificationCode = verificationService.createEmailVerificationToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful. Please verify your email with the code provided.");
        response.put("email", user.getEmail());
        response.put("userId", user.getId());
        response.put("customerId", customer.getId());
        // Returned for demo / front-end; in production this would only be emailed
        response.put("verificationCode", verificationCode);
        response.put("note", "In production the verification code is sent by email only.");

        return response;
    }

    // ─────────────────────────────────────────────────────────────
    // Verify email
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse verifyEmail(String code) {
        User user = verificationService.verifyEmail(code);

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
                "If an account with that email exists, a password reset code has been generated.");

        // For the student demo we return the code when the email is valid
        codeOpt.ifPresent(code -> {
            response.put("resetCode", code);
            response.put("note", "In production the reset code is sent by email only.");
        });

        return response;
    }

    // ─────────────────────────────────────────────────────────────
    // Reset password
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> resetPassword(String code, String newPassword) {
        // Validate strength first
        passwordPolicy.enforce(newPassword);

        User user = verificationService.validatePasswordResetCode(code);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userService.save(user);

        verificationService.consumePasswordResetToken(code);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password has been reset successfully. You can now log in with your new password.");
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
