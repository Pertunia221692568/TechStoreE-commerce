/* ICustomerRepositoryTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Customer;
import za.ac.cput.factory.CustomerFactory;
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

    private Customer savedCustomer;

    @BeforeEach
    void setUp() {
        // Use unique email each run to avoid constraint violations
        Customer customer = CustomerFactory.create(
                "Pertunia", "Sifunda",
                "pertunia" + System.currentTimeMillis() + "@test.co.za",
                "071 000 1111", "10 Long Street, Cape Town");
        savedCustomer = customerRepository.save(customer);
    }

    @Test
    @DisplayName("Should save and find Customer by ID")
    void shouldSaveAndFindById() {
        Optional<Customer> found = customerRepository.findById(savedCustomer.getId());

        assertTrue(found.isPresent());
        assertEquals(savedCustomer.getEmail(), found.get().getEmail());
        assertEquals("Pertunia", found.get().getFirstName());
    }

    @Test
    @DisplayName("Should find Customer by email")
    void shouldFindByEmail() {
        Optional<Customer> found = customerRepository.findByEmail(savedCustomer.getEmail());

        assertTrue(found.isPresent());
        assertEquals(savedCustomer.getId(), found.get().getId());
    }

    @Test
    @DisplayName("existsByEmail should return true for existing email")
    void existsByEmail_shouldReturnTrue() {
        assertTrue(customerRepository.existsByEmail(savedCustomer.getEmail()));
    }

    @Test
    @DisplayName("existsByEmail should return false for unknown email")
    void existsByEmail_shouldReturnFalse() {
        assertFalse(customerRepository.existsByEmail("doesnotexist@test.com"));
    }
}

