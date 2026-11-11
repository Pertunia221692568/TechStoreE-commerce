package za.ac.cput.security.verification;

import jakarta.persistence.*;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;

/**
 * One-time, short-lived token used for the "forgot password" flow.
 * After a successful password change the token is marked as used
 * so it cannot be replayed.
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    /** Optional 6-digit code for front-end convenience */
    @Column(nullable = false, length = 6)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected PasswordResetToken() {}

    public PasswordResetToken(String token, String code, User user, LocalDateTime expiresAt) {
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
}
