package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logins")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String passwordHash;
    private LocalDateTime lastPasswordDate;
    private LocalDateTime lastLoginDate;

    public Login() {
    }

    public Login(String username, String passwordHash,
                 LocalDateTime lastPasswordDate,
                 LocalDateTime lastLoginDate) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.lastPasswordDate = lastPasswordDate;
        this.lastLoginDate = lastLoginDate;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getLastPasswordDate() { return lastPasswordDate; }
    public void setLastPasswordDate(LocalDateTime lastPasswordDate) { this.lastPasswordDate = lastPasswordDate; }

    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }
}