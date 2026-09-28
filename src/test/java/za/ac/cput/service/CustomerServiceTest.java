/* CustomerServiceTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.service;

import za.ac.cput.domain.Customer;
import za.ac.cput.repository.ICustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ICustomerRepository customerRepository;

    private String uniqueEmail;

    @BeforeEach
    void setUp() {
        uniqueEmail = "test" + System.currentTimeMillis() + "@test.co.za";
    }

    @Test
    @DisplayName("register should save a new customer")
    void register_shouldSaveCustomer() {
        Customer saved = customerService.register(
                "Pertunia", "Sifunda", uniqueEmail,
                "071 000 1111", "10 Long Street, Cape Town");

        assertNotNull(saved.getId());
        assertEquals("Pertunia", saved.getFirstName());
        assertEquals(uniqueEmail, saved.getEmail());
    }

    @Test
    @DisplayName("register should throw when email already exists")
    void register_shouldThrowWhenEmailExists() {
        customerService.register("First", "User", uniqueEmail, "", "");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                customerService.register("Second", "User", uniqueEmail, "", ""));

        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    @DisplayName("findById should return customer when found")
    void findById_shouldReturnCustomer() {
        Customer saved = customerService.register(
                "Jane", "Doe", uniqueEmail, "082 111 2222", "Cape Town");

        Optional<Customer> found = customerService.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
        assertEquals(uniqueEmail, found.get().getEmail());
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void findById_shouldReturnEmpty() {
        Optional<Customer> found = customerService.findById(999999L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findByEmail should return customer")
    void findByEmail_shouldReturnCustomer() {
        customerService.register("John", "Smith", uniqueEmail, "", "");

        Optional<Customer> found = customerService.findByEmail(uniqueEmail);

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    @DisplayName("update should modify existing customer")
    void update_shouldModifyCustomer() {
        Customer saved = customerService.register(
                "Old", "Name", uniqueEmail, "071 000 0000", "Old Address");

        Customer updated = customerService.update(
                saved.getId(), "NewFirst", "NewLast",
                "updated" + System.currentTimeMillis() + "@test.com",
                "083 111 1111", "New Address");

        assertEquals("NewFirst", updated.getFirstName());
        assertEquals("NewLast", updated.getLastName());
        assertEquals("New Address", updated.getAddress());
    }

    @Test
    @DisplayName("update should throw when customer not found")
    void update_shouldThrowWhenNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                customerService.update(999999L, "A", "B", "c@d.com", "", ""));
    }

    @Test
    @DisplayName("findAll should return list containing registered customers")
    void findAll_shouldReturnCustomers() {
        customerService.register("Test", "User", uniqueEmail, "", "");

        List<Customer> all = customerService.findAll();

        assertFalse(all.isEmpty());
        assertTrue(all.stream().anyMatch(c -> c.getEmail().equals(uniqueEmail)));
    }

    @Test
    @DisplayName("delete should remove customer")
    void delete_shouldRemoveCustomer() {
        Customer saved = customerService.register(
                "ToDelete", "User", uniqueEmail, "", "");

        customerService.delete(saved.getId());

        Optional<Customer> found = customerService.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}

