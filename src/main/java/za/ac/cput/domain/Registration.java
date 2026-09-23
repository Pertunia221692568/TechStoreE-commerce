package za.ac.cput.domain;
import java.time.LocalDateTime;
import java.util.Objects;

public class Registration {
    private String registrationId;
    private String email;
    private String fullName;
    private String passwordHash;
    private LocalDateTime registrationDate;

    public Registration() {
    }

    public Registration(String registrationId, String email,
                        String fullName, String passwordHash,
                        LocalDateTime registrationDate) {
        this.registrationId = registrationId;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.registrationDate = registrationDate;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
