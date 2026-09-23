/* CustomerFactory.java
 * Factory – DDD Building Block
 * TechStore E-Commerce – ADP372S
 * Author: Pertunia (221692568)
 * Date: 2026
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Customer;

/**
 * Author :Pertunia Sifunda(221692568)
 */
public class CustomerFactory {

    public static Customer create(String firstName, String lastName,
                                  String email, String phone, String address) {
        return new Customer.Builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
    }


    public static Customer createBasic(String firstName, String lastName, String email) {
        return new Customer.Builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }
}

