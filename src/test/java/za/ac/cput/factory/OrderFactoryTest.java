/* OrderFactoryTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the creation of Order objects via OrderFactory.
 */
class OrderFactoryTest {

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = CustomerFactory.create(
                "Pertunia", "Sifunda", "pertunia@test.co.za",
                "071 000 1111", "10 Long Street, Cape Town");
    }

    @Test
    @DisplayName("Should create Order with PENDING status by default")
    void shouldCreateOrderWithPendingStatus() {
        Order order = OrderFactory.create(testCustomer, "10 Long Street, Cape Town");

        assertNotNull(order, "Order should not be null");
        assertEquals(Order.Status.PENDING, order.getStatus(),
                "Default status should be PENDING");
    }

    @Test
    @DisplayName("Should create Order linked to the correct customer")
    void shouldCreateOrderWithCorrectCustomer() {
        Order order = OrderFactory.create(testCustomer, "10 Long Street, Cape Town");

        assertNotNull(order.getCustomer(), "Customer should not be null");
        assertEquals("Pertunia Sifunda", order.getCustomer().getFullName());
        assertEquals("pertunia@test.co.za", order.getCustomer().getEmail());
    }

    @Test
    @DisplayName("Should create Order with the correct shipping address")
    void shouldCreateOrderWithShippingAddress() {
        String address = "15 Bree Street, Cape Town, 8001";
        Order order = OrderFactory.create(testCustomer, address);

        assertEquals(address, order.getShippingAddress());
    }

    @Test
    @DisplayName("Should set orderDate automatically on creation")
    void shouldSetOrderDateAutomatically() {
        Order order = OrderFactory.create(testCustomer, "Test Address");

        assertNotNull(order.getOrderDate(), "Order date should be set automatically");
    }

    @Test
    @DisplayName("Should create Order with zero total amount initially")
    void shouldCreateOrderWithZeroTotal() {
        Order order = OrderFactory.create(testCustomer, "Test Address");

        assertEquals(0, order.getTotalAmount().compareTo(java.math.BigDecimal.ZERO),
                "Total should be R0.00 before items are added");
    }

    @Test
    @DisplayName("Should create Order using customer's saved address")
    void shouldCreateOrderWithCustomerAddress() {
        Order order = OrderFactory.createWithCustomerAddress(testCustomer);

        assertEquals(testCustomer.getAddress(), order.getShippingAddress());
    }

    @Test
    @DisplayName("Should create Order with notes")
    void shouldCreateOrderWithNotes() {
        Order order = OrderFactory.createWithNotes(
                testCustomer, "15 Main Road", "Please leave at the gate");

        assertEquals("Please leave at the gate", order.getNotes());
    }

    @Test
    @DisplayName("Should throw exception when customer is null")
    void shouldThrowWhenCustomerIsNull() {
        assertThrows(IllegalStateException.class,
                () -> OrderFactory.create(null, "Some Address"),
                "Should throw IllegalStateException when customer is null");
    }

    @Test
    @DisplayName("New order should be cancellable")
    void newOrderShouldBeCancellable() {
        Order order = OrderFactory.create(testCustomer, "Test Address");
        assertTrue(order.isCancellable(), "A new PENDING order should be cancellable");
    }
}

