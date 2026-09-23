/* IOrderRepositoryTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Order;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.factory.OrderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IOrderRepositoryTest {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    private Customer savedCustomer;

    @BeforeEach
    void setUp() {
        // Clean previous data to avoid unique constraint errors
        orderRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = CustomerFactory.create(
                "Pertunia", "Sifunda",
                "pertunia" + System.currentTimeMillis() + "@test.co.za",
                "071 000 1111", "10 Long Street, Cape Town");
        savedCustomer = customerRepository.save(customer);
    }

    @Test
    @DisplayName("Should save and retrieve an Order by ID")
    void shouldSaveAndFindOrderById() {
        Order order = OrderFactory.create(savedCustomer, "10 Long Street, Cape Town");
        Order saved = orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(saved.getId());

        assertTrue(found.isPresent(), "Order should be found by ID");
        assertEquals(Order.Status.PENDING, found.get().getStatus());
        assertEquals(savedCustomer.getEmail(), found.get().getCustomer().getEmail());
    }

    @Test
    @DisplayName("Should find all orders for a specific customer")
    void shouldFindOrdersByCustomerId() {
        orderRepository.save(OrderFactory.create(savedCustomer, "Address 1"));
        orderRepository.save(OrderFactory.create(savedCustomer, "Address 2"));
        orderRepository.save(OrderFactory.create(savedCustomer, "Address 3"));

        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(savedCustomer.getId());

        assertEquals(3, orders.size(), "Should find 3 orders for this customer");
    }

    @Test
    @DisplayName("Should find orders by status PENDING")
    void shouldFindOrdersByStatus() {
        orderRepository.save(OrderFactory.create(savedCustomer, "Address A"));
        orderRepository.save(OrderFactory.create(savedCustomer, "Address B"));

        List<Order> pendingOrders = orderRepository.findByStatus(Order.Status.PENDING);

        assertFalse(pendingOrders.isEmpty(), "Should find PENDING orders");
        pendingOrders.forEach(o ->
                assertEquals(Order.Status.PENDING, o.getStatus()));
    }

    @Test
    @DisplayName("Should return all orders")
    void shouldReturnAllOrders() {
        orderRepository.save(OrderFactory.create(savedCustomer, "Addr 1"));
        orderRepository.save(OrderFactory.create(savedCustomer, "Addr 2"));

        List<Order> allOrders = orderRepository.findAll();

        assertTrue(allOrders.size() >= 2, "Should return at least 2 orders");
    }

    @Test
    @DisplayName("Should delete an order by ID")
    void shouldDeleteOrderById() {
        Order order = orderRepository.save(OrderFactory.create(savedCustomer, "Test Address"));
        Long id = order.getId();

        orderRepository.deleteById(id);

        Optional<Order> found = orderRepository.findById(id);
        assertFalse(found.isPresent(), "Order should no longer exist after deletion");
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        Order order = orderRepository.save(OrderFactory.create(savedCustomer, "Test Address"));
        order.setStatus(Order.Status.CONFIRMED);
        Order updated = orderRepository.save(order);

        assertEquals(Order.Status.CONFIRMED, updated.getStatus());
    }

    @Test
    @DisplayName("Should find orders by customer ID and status")
    void shouldFindByCustomerAndStatus() {
        Order o1 = orderRepository.save(OrderFactory.create(savedCustomer, "Addr 1"));
        o1.setStatus(Order.Status.SHIPPED);
        orderRepository.save(o1);
        orderRepository.save(OrderFactory.create(savedCustomer, "Addr 2")); // PENDING

        List<Order> shipped = orderRepository.findByCustomerIdAndStatus(
                savedCustomer.getId(), Order.Status.SHIPPED);

        assertEquals(1, shipped.size());
        assertEquals(Order.Status.SHIPPED, shipped.get(0).getStatus());
    }
}