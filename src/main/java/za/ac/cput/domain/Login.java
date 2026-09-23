package za.ac.cput.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Login {
    private String loginId;
    private String username;
    private String passwordHash;
    private LocalDateTime lastPasswordDate;
    private LocalDateTime lastLoginDate;

    public Login() {
    }

    public Login(String loginId, String username, String passwordHash,
                 LocalDateTime lastPasswordDate,
                 LocalDateTime lastLoginDate) {

        this.loginId = loginId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.lastPasswordDate = lastPasswordDate;
        this.lastLoginDate = lastLoginDate;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getLastPasswordDate() {
        return lastPasswordDate;
    }

    public void setLastPasswordDate(LocalDateTime lastPasswordDate) {
        this.lastPasswordDate = lastPasswordDate;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}

