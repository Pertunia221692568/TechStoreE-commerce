/* OrderServiceTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.service;

import za.ac.cput.domain.*;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.factory.ProductsFactory;
import za.ac.cput.repository.ICustomerRepository;
import za.ac.cput.repository.IOrderRepository;
import za.ac.cput.repository.IProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IProductsRepository productRepository;

    @Autowired
    private IOrderRepository orderRepository;

    private Customer customer;
    private Products product;

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(CustomerFactory.create(
                "Pertunia", "Sifunda",
                "order-test-" + System.currentTimeMillis() + "@test.co.za",
                "071 000 1111", "10 Long Street, Cape Town"));

        product = productRepository.save(ProductsFactory.create(
                "Test Laptop " + System.currentTimeMillis(),
                "Dell", "Test description",
                new BigDecimal("15000.00"), 10, "Laptop"));
    }

    @Test
    @DisplayName("placeOrder should create order and reduce stock")
    void placeOrder_shouldCreateOrderAndReduceStock() {
        int stockBefore = product.getStockQuantity();

        Order order = orderService.placeOrder(
                customer.getId(),
                "10 Long Street, Cape Town",
                List.of(product.getId()),
                List.of(2));

        assertNotNull(order.getId());
        assertEquals(Order.Status.PENDING, order.getStatus());
        assertEquals(1, order.getOrderItems().size());
        assertEquals(customer.getId(), order.getCustomer().getId());

        Products updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(stockBefore - 2, updatedProduct.getStockQuantity());
    }

    @Test
    @DisplayName("placeOrder should throw when customer not found")
    void placeOrder_shouldThrowWhenCustomerNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.placeOrder(999999L, "Address",
                        List.of(product.getId()), List.of(1)));
    }

    @Test
    @DisplayName("placeOrder should throw when product list is empty")
    void placeOrder_shouldThrowWhenNoProducts() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.placeOrder(customer.getId(), "Address",
                        List.of(), List.of()));
    }

    @Test
    @DisplayName("placeOrder should throw when not enough stock")
    void placeOrder_shouldThrowWhenInsufficientStock() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.placeOrder(customer.getId(), "Address",
                        List.of(product.getId()), List.of(999)));
    }

    @Test
    @DisplayName("findById should return order when found")
    void findById_shouldReturnOrder() {
        Order created = orderService.placeOrder(
                customer.getId(), "10 Long Street",
                List.of(product.getId()), List.of(1));

        Optional<Order> found = orderService.findById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(Order.Status.PENDING, found.get().getStatus());
    }

    @Test
    @DisplayName("findByCustomer should return customer orders")
    void findByCustomer_shouldReturnOrders() {
        orderService.placeOrder(customer.getId(), "Addr 1",
                List.of(product.getId()), List.of(1));
        orderService.placeOrder(customer.getId(), "Addr 2",
                List.of(product.getId()), List.of(1));

        List<Order> orders = orderService.findByCustomer(customer.getId());

        assertTrue(orders.size() >= 2);
    }

    @Test
    @DisplayName("updateStatus should change order status")
    void updateStatus_shouldChangeStatus() {
        Order created = orderService.placeOrder(
                customer.getId(), "10 Long Street",
                List.of(product.getId()), List.of(1));

        Order updated = orderService.updateStatus(created.getId(), Order.Status.CONFIRMED);

        assertEquals(Order.Status.CONFIRMED, updated.getStatus());
    }

    @Test
    @DisplayName("cancelOrder should set CANCELLED and restore stock")
    void cancelOrder_shouldCancelAndRestoreStock() {
        Order created = orderService.placeOrder(
                customer.getId(), "10 Long Street",
                List.of(product.getId()), List.of(3));

        int stockAfterOrder = productRepository.findById(product.getId())
                .orElseThrow().getStockQuantity();

        Order cancelled = orderService.cancelOrder(created.getId());

        assertEquals(Order.Status.CANCELLED, cancelled.getStatus());

        Products afterCancel = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(stockAfterOrder + 3, afterCancel.getStockQuantity());
    }

    @Test
    @DisplayName("cancelOrder should throw when order is not cancellable")
    void cancelOrder_shouldThrowWhenNotCancellable() {
        Order created = orderService.placeOrder(
                customer.getId(), "10 Long Street",
                List.of(product.getId()), List.of(1));

        orderService.updateStatus(created.getId(), Order.Status.SHIPPED);

        assertThrows(IllegalStateException.class, () ->
                orderService.cancelOrder(created.getId()));
    }

    @Test
    @DisplayName("findByStatus should return orders with given status")
    void findByStatus_shouldReturnMatchingOrders() {
        orderService.placeOrder(customer.getId(), "Addr",
                List.of(product.getId()), List.of(1));

        List<Order> pending = orderService.findByStatus(Order.Status.PENDING);

        assertFalse(pending.isEmpty());
        pending.forEach(o -> assertEquals(Order.Status.PENDING, o.getStatus()));
    }

    @Test
    @DisplayName("getTotalRevenue should return non-negative value")
    void getTotalRevenue_shouldReturnValue() {
        BigDecimal revenue = orderService.getTotalRevenue();
        assertNotNull(revenue);
        assertTrue(revenue.compareTo(BigDecimal.ZERO) >= 0);
    }
}

