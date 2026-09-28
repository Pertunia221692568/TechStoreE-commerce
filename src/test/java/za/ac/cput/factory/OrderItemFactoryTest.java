/* OrderItemFactoryTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Order;
import za.ac.cput.domain.OrderItem;
import za.ac.cput.domain.Products;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemFactoryTest {

    private Order order;
    private Products product;

    @BeforeEach
    void setUp() {
        Customer customer = CustomerFactory.create(
                "Pertunia", "Sifunda", "pertunia@test.co.za",
                "071 000 1111", "10 Long Street");
        order = OrderFactory.create(customer, "10 Long Street");
        product = ProductsFactory.create(
                "Test Laptop", "Dell", "Test description",
                new BigDecimal("15000.00"), 10, "Laptop");
    }

    @Test
    @DisplayName("Should create OrderItem with correct product, quantity and unit price")
    void shouldCreateOrderItem() {
        OrderItem item = OrderItemFactory.create(order, product, 2);

        assertNotNull(item);
        assertEquals(product, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(0, new BigDecimal("15000.00").compareTo(item.getUnitPrice()));
        assertEquals(0, new BigDecimal("30000.00").compareTo(item.getSubtotal()));
    }

    @Test
    @DisplayName("Should throw when quantity is zero or negative")
    void shouldThrowWhenQuantityInvalid() {
        assertThrows(IllegalStateException.class, () ->
                OrderItemFactory.create(order, product, 0));
        assertThrows(IllegalStateException.class, () ->
                OrderItemFactory.create(order, product, -1));
    }

    @Test
    @DisplayName("Should throw when product is null")
    void shouldThrowWhenProductNull() {
        assertThrows(IllegalStateException.class, () ->
                OrderItemFactory.create(order, null, 1));
    }
}