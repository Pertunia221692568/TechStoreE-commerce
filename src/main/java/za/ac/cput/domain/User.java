/* User class
 * TechStoreE-commerce – ADP372S
 * Common identity shared by Customer and Admin.
 * Contains authentication-related and shared personal fields.
 */
package za.ac.cput.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore                     // never return password hash to clients
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.CUSTOMER;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    // Optional bidirectional link back to Customer (only when role = CUSTOMER)
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Customer customer;

    protected User() {}

    private User(Builder builder) {
        this.email        = builder.email;
        this.passwordHash = builder.passwordHash;
        this.firstName    = builder.firstName;
        this.lastName     = builder.lastName;
        this.phone        = builder.phone;
        this.role         = builder.role != null ? builder.role : Role.CUSTOMER;
        this.enabled      = builder.enabled;
        this.createdAt    = LocalDateTime.now();
    }

    // ── Getters ───────────────────────────────────────────────
    public Long getId()                   { return id; }
    public String getEmail()              { return email; }
    public String getPasswordHash()       { return passwordHash; }
    public String getFirstName()          { return firstName; }
    public String getLastName()           { return lastName; }
    public String getPhone()              { return phone; }
    public Role getRole()                 { return role; }
    public boolean isEnabled()            { return enabled; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public Customer getCustomer()         { return customer; }
    public String getFullName()           { return firstName + " " + lastName; }

    // ── Setters ───────────────────────────────────────────────
    public void setEmail(String email)                { this.email = email; }
    public void setPasswordHash(String passwordHash)  { this.passwordHash = passwordHash; }
    public void setFirstName(String firstName)        { this.firstName = firstName; }
    public void setLastName(String lastName)          { this.lastName = lastName; }
    public void setPhone(String phone)                { this.phone = phone; }
    public void setRole(Role role)                    { this.role = role; }
    public void setEnabled(boolean enabled)           { this.enabled = enabled; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setCustomer(Customer customer)        { this.customer = customer; }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role +
                ", name='" + getFullName() + "'}";
    }

    // ── Builder ───────────────────────────────────────────────
    public static class Builder {
        private String email;
        private String passwordHash;
        private String firstName;
        private String lastName;
        private String phone;
        private Role role = Role.CUSTOMER;
        private boolean enabled = true;

        public Builder email(String v)         { this.email = v; return this; }
        public Builder passwordHash(String v)  { this.passwordHash = v; return this; }
        public Builder firstName(String v)     { this.firstName = v; return this; }
        public Builder lastName(String v)      { this.lastName = v; return this; }
        public Builder phone(String v)         { this.phone = v; return this; }
        public Builder role(Role v)            { this.role = v; return this; }
        public Builder enabled(boolean v)      { this.enabled = v; return this; }

        public User build() {
            if (email == null || email.isBlank())
                throw new IllegalStateException("Email is required");
            if (passwordHash == null || passwordHash.isBlank())
                throw new IllegalStateException("Password hash is required");
            if (firstName == null || firstName.isBlank())
                throw new IllegalStateException("First name is required");
            if (lastName == null || lastName.isBlank())
                throw new IllegalStateException("Last name is required");
            return new User(this);
        }
    }
}
