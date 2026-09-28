/* CustomerFactoryTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerFactoryTest {

    @Test
    @DisplayName("Should create Customer with all fields")
    void shouldCreateCustomerWithAllFields() {
        Customer customer = CustomerFactory.create(
                "Pertunia", "Sifunda", "pertunia@test.co.za",
                "071 000 1111", "10 Long Street, Cape Town");

        assertNotNull(customer);
        assertEquals("Pertunia", customer.getFirstName());
        assertEquals("Sifunda", customer.getLastName());
        assertEquals("pertunia@test.co.za", customer.getEmail());
        assertEquals("071 000 1111", customer.getPhone());
        assertEquals("10 Long Street, Cape Town", customer.getAddress());
        assertEquals("Pertunia Sifunda", customer.getFullName());
    }

    @Test
    @DisplayName("Should create basic Customer with only required fields")
    void shouldCreateBasicCustomer() {
        Customer customer = CustomerFactory.createBasic(
                "John", "Doe", "john@example.com");

        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john@example.com", customer.getEmail());
        assertNull(customer.getPhone());
        assertNull(customer.getAddress());
    }

    @Test
    @DisplayName("Should throw when first name is blank")
    void shouldThrowWhenFirstNameBlank() {
        assertThrows(IllegalStateException.class, () ->
                CustomerFactory.create("", "Sifunda", "test@test.com", "", ""));
    }

    @Test
    @DisplayName("Should throw when email is blank")
    void shouldThrowWhenEmailBlank() {
        assertThrows(IllegalStateException.class, () ->
                CustomerFactory.create("Pertunia", "Sifunda", "", "", ""));
    }
}
