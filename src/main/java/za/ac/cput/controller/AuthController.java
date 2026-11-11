package za.ac.cput.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.User;
import za.ac.cput.dto.auth.*;
import za.ac.cput.security.auth.AuthService;
import za.ac.cput.service.UserService;

import java.util.Map;

/**
 * Authentication endpoints.
 * All business logic lives in AuthService – this controller only handles HTTP.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        } catch (IllegalStateException ex) {
            // Account not yet verified
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Registers a new customer.
     * The account is created in a DISABLED state.
     * A 6-digit verification code is returned (in production it would be emailed).
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Map<String, Object> result = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Verifies the email using the 6-digit code.
     * On success the account is enabled and a JWT is returned (user is logged in).
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        try {
            AuthResponse response = authService.verifyEmail(request.getCode());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Starts the forgot-password flow.
     * Always returns a generic success message (does not leak whether the email exists).
     * For demo purposes the reset code is also returned when the email is valid.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        Map<String, Object> result = authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(result);
    }

    /**
     * Completes the password reset using the 6-digit code and a new strong password.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            Map<String, Object> result = authService.resetPassword(
                    request.getCode(), request.getNewPassword());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long customerId = null;
        if (user.getRole() == za.ac.cput.domain.Role.CUSTOMER) {
            customerId = user.getCustomer() != null ? user.getCustomer().getId() : null;
        }

        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "role", user.getRole().name(),
                "customerId", customerId != null ? customerId : "",
                "enabled", user.isEnabled()
        ));
    }
}
