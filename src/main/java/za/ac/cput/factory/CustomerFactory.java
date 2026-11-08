/* CustomerFactory.java
 * Factory – DDD Building Block
 * TechStore E-Commerce – ADP372S
 * Author: Pertunia (221692568)
 * Date: 2026
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.User;

/**
 * Author :Pertunia Sifunda(221692568)
 */
public class CustomerFactory {

    /**
     * Preferred way: create a Customer that is linked to an existing User.
     */
    public static Customer create(User user, String address) {
        return new Customer.Builder()
                .user(user)
                .address(address)
                .build();
    }

    /**
     * Convenience overload when address is not yet known.
     */
    public static Customer create(User user) {
        return create(user, null);
    }

    /**
     * Convenience for tests and simple scenarios.
     * Creates a User (CUSTOMER role) + Customer in one step.
     * Password is a temporary placeholder.
     */
    public static Customer create(String firstName, String lastName,
                                  String email, String phone, String address) {
        User user = UserFactory.createCustomerUser(
                email,
                "TEMP_HASH_" + email.hashCode(),
                firstName,
                lastName,
                phone
        );
        return create(user, address);
    }

    /**
     * Simple create with only the required fields (used by older tests).
     */
    public static Customer createBasic(String firstName, String lastName, String email) {
        return create(firstName, lastName, email, null, null);
    }
}