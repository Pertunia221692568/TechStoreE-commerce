/* UserFactory.java
 * Factory – DDD Building Block
 * TechStore E-Commerce – ADP372S
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;

/**
 * Creates User instances. Password must already be hashed by the caller
 * (or by a future PasswordEncoder service).
 */
public class UserFactory {

    public static User createCustomerUser(String email, String passwordHash,
                                          String firstName, String lastName, String phone) {
        return new User.Builder()
                .email(email)
                .passwordHash(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
    }

    public static User createAdminUser(String email, String passwordHash,
                                       String firstName, String lastName) {
        return new User.Builder()
                .email(email)
                .passwordHash(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .role(Role.ADMIN)
                .enabled(true)
                .build();
    }

    public static User create(String email, String passwordHash,
                              String firstName, String lastName,
                              String phone, Role role) {
        return new User.Builder()
                .email(email)
                .passwordHash(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(role != null ? role : Role.CUSTOMER)
                .enabled(true)
                .build();
    }
}
