package za.ac.cput.security.verification;

import jakarta.persistence.*;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;

/**
 * One-time token used to verify a newly registered customer's email address.
 * The account remains disabled until this token is successfully consumed.
 */
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    /** 6-digit code that the user types in (easier for front-end) */
    @Column(nullable = false, length = 6)
    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    /** Wrong guesses so far; the code is locked after too many. */
    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected EmailVerificationToken() {}

    public EmailVerificationToken(String token, String code, User user, LocalDateTime expiresAt) {
        this.token = token;
        this.code = code;
        this.user = user;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    // Getters
    public Long getId() { return id; }
    public String getToken() { return token; }
    public String getCode() { return code; }
    public User getUser() { return user; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setUsed(boolean used) { this.used = used; }
    public int getAttempts() { return attempts; }
    public void incrementAttempts() { this.attempts++; }
}
