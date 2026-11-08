/* ICustomerRepositoryTest.java
 * Author: Pertunia Sifunda (221692568)
 * Updated for User composition.
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.User;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.factory.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ICustomerRepositoryTest {

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IUserRepository userRepository;

    private Customer savedCustomer;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "pertunia" + System.currentTimeMillis() + "@test.co.za";

        // 1. Create and save User first
        User user = UserFactory.createCustomerUser(
                testEmail,
                "TEMP_HASH",
                "Pertunia",
                "Sifunda",
                "071 000 1111"
        );
        user = userRepository.save(user);

        // 2. Create Customer linked to that User
        Customer customer = CustomerFactory.create(user, "10 Long Street, Cape Town");
        savedCustomer = customerRepository.save(customer);
    }

    @Test
    @DisplayName("Should save and find Customer by ID")
    void shouldSaveAndFindById() {
        Optional<Customer> found = customerRepository.findById(savedCustomer.getId());

        assertTrue(found.isPresent());
        assertEquals(testEmail, found.get().getEmail());
        assertEquals("Pertunia", found.get().getFirstName());
    }

    @Test
    @DisplayName("Should find Customer by email (via User)")
    void shouldFindByUserEmail() {
        Optional<Customer> found = customerRepository.findByUser_Email(testEmail);

        assertTrue(found.isPresent());
        assertEquals(savedCustomer.getId(), found.get().getId());
    }

    @Test
    @DisplayName("existsByUser_Email should return true for existing email")
    void existsByUserEmail_shouldReturnTrue() {
        assertTrue(customerRepository.existsByUser_Email(testEmail));
    }

    @Test
    @DisplayName("existsByUser_Email should return false for unknown email")
    void existsByUserEmail_shouldReturnFalse() {
        assertFalse(customerRepository.existsByUser_Email("doesnotexist@test.com"));
    }
}