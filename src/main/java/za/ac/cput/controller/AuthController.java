package za.ac.cput.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;
import za.ac.cput.dto.auth.AuthResponse;
import za.ac.cput.dto.auth.LoginRequest;
import za.ac.cput.dto.auth.RegisterRequest;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.repository.ICustomerRepository;
import za.ac.cput.security.token.JwtService;
import za.ac.cput.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ICustomerRepository customerRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          ICustomerRepository customerRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().trim().toLowerCase(),
                            request.getPassword()
                    )
            );

            User user = userService.findByEmail(request.getEmail().trim().toLowerCase())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            userService.updateLastLogin(user.getId());

            String token = jwtService.generateToken(user);
            Long customerId = resolveCustomerId(user);

            return ResponseEntity.ok(new AuthResponse(
                    token, user.getId(), user.getEmail(),
                    user.getFirstName(), user.getLastName(),
                    user.getRole(), customerId
            ));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userService.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = userService.createCustomerUser(
                email,
                hashedPassword,
                request.getFirstName().trim(),
                request.getLastName().trim(),
                request.getPhone() != null ? request.getPhone().trim() : null
        );

        Customer customer = CustomerFactory.create(user, request.getAddress());
        customer = customerRepository.save(customer);

        String token = jwtService.generateToken(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(
                token, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getRole(), customer.getId()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long customerId = resolveCustomerId(user);

        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "role", user.getRole().name(),
                "customerId", customerId != null ? customerId : ""
        ));
    }

    private Long resolveCustomerId(User user) {
        if (user.getRole() != Role.CUSTOMER) return null;
        return customerRepository.findByUser_Id(user.getId())
                .map(Customer::getId)
                .orElse(null);
    }
}