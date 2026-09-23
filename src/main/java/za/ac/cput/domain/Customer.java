/* Customer class
 * TechStoreE-commerce – ADP372S
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    protected Customer() {}

    private Customer(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName  = builder.lastName;
        this.email     = builder.email;
        this.phone     = builder.phone;
        this.address   = builder.address;
    }

    // ── Getters ───────────────────────────────────────────────
    public Long getId()            { return id; }
    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getEmail()       { return email; }
    public String getPhone()       { return phone; }
    public String getAddress()     { return address; }
    public List<Order> getOrders() { return orders; }
    public String getFullName()    { return firstName + " " + lastName; }

    // ── Setters ───────────────────────────────────────────────
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }
    public void setEmail(String email)         { this.email = email; }
    public void setPhone(String phone)         { this.phone = phone; }
    public void setAddress(String address)     { this.address = address; }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + firstName + " " + lastName +
                "', email='" + email + "'}";
    }

    // ── Builder ───────────────────────────────────────────────
    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;

        public Builder firstName(String v) { this.firstName = v; return this; }
        public Builder lastName(String v)  { this.lastName = v; return this; }
        public Builder email(String v)     { this.email = v; return this; }
        public Builder phone(String v)     { this.phone = v; return this; }
        public Builder address(String v)   { this.address = v; return this; }

        public Customer build() {
            if (firstName == null || firstName.isBlank())
                throw new IllegalStateException("First name is required");
            if (lastName == null || lastName.isBlank())
                throw new IllegalStateException("Last name is required");
            if (email == null || email.isBlank())
                throw new IllegalStateException("Email is required");
            return new Customer(this);
        }
    }
}

