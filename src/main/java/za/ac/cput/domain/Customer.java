/* Customer class
 * TechStoreE-commerce – ADP372S
 * Author: Pertunia Sifunda (221692568)
 *
 * Customer is linked to a User (composition).
 * Shared identity fields live on User; Customer holds customer-specific data.
 */
package za.ac.cput.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Link to the common User identity.
     * One User can have at most one Customer profile.
     */
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Customer-specific address (shipping / billing default) */
    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore   // prevents infinite Customer ↔ Order recursion
    private List<Order> orders = new ArrayList<>();

    protected Customer() {}

    private Customer(Builder builder) {
        this.user    = builder.user;
        this.address = builder.address;
    }

    // ── Getters ───────────────────────────────────────────────
    public Long getId()            { return id; }
    public User getUser()          { return user; }
    public String getAddress()     { return address; }
    @JsonIgnore
    public List<Order> getOrders() { return orders; }

    // Convenience getters that delegate to User (so existing frontend code still works)
    public String getFirstName()   { return user != null ? user.getFirstName() : null; }
    public String getLastName()    { return user != null ? user.getLastName()  : null; }
    public String getEmail()       { return user != null ? user.getEmail()     : null; }
    public String getPhone()       { return user != null ? user.getPhone()     : null; }
    public String getFullName()    { return user != null ? user.getFullName()  : null; }

    // ── Setters ───────────────────────────────────────────────
    public void setUser(User user)         { this.user = user; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "Customer{id=" + id +
                ", user=" + (user != null ? user.getEmail() : "null") +
                ", address='" + address + "'}";
    }

    // ── Builder ───────────────────────────────────────────────
    public static class Builder {
        private User user;
        private String address;

        public Builder user(User v)      { this.user = v; return this; }
        public Builder address(String v) { this.address = v; return this; }

        public Customer build() {
            if (user == null)
                throw new IllegalStateException("User is required for a Customer");
            return new Customer(this);
        }
    }
}