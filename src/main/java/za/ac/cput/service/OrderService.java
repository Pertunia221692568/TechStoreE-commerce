/* OrderService.java
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.service;

import za.ac.cput.domain.*;
import za.ac.cput.factory.OrderFactory;
import za.ac.cput.factory.OrderItemFactory;
import za.ac.cput.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class OrderService {

    private final IOrderRepository     orderRepository;
    private final ICustomerRepository  customerRepository;
    private final IProductsRepository   productRepository;

    public OrderService(IOrderRepository orderRepository,
                        ICustomerRepository customerRepository,
                        IProductsRepository productRepository) {
        this.orderRepository    = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository  = productRepository;
    }

    /** Returns all orders. */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /** Finds an order by ID. */
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    /** Returns all orders for a specific customer. */
    public List<Order> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    /** Returns all orders with a specific status. */
    public List<Order> findByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Places a new order.
     * Validates stock, creates order items, reduces stock,
     * and saves everything in one transaction.
     */
    public Order placeOrder(Long customerId, String shippingAddress,
                            List<Long> productIds, List<Integer> quantities) {

        // 1. Find customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + customerId));

        // 2. Create order using factory
        Order order = OrderFactory.create(customer, shippingAddress);

        // 3. Add each product as an order item
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            int qty = quantities.get(i);

            Products product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found with ID: " + productId));

            // Reduce stock (throws if not enough)
            product.reduceStock(qty);
            productRepository.save(product);

            // Create order item using factory
            OrderItem item = OrderItemFactory.create(order, product, qty);
            order.addOrderItem(item);
        }

        return orderRepository.save(order);
    }

    /** Updates the status of an order. */
    public Order updateStatus(Long orderId, Order.Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    /** Cancels an order and restores product stock. */
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId));

        if (!order.isCancellable()) {
            throw new IllegalStateException(
                    "Order #" + orderId + " cannot be cancelled. Status: " + order.getStatus());
        }

        // Restore stock for each item
        for (OrderItem item : order.getOrderItems()) {
            Products product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(Order.Status.CANCELLED);
        return orderRepository.save(order);
    }

    /** Deletes an order by ID. */
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    /** Counts orders by status. */
    public long countByStatus(Order.Status status) {
        return orderRepository.findByStatus(status).size();
    }

    /** Returns total revenue from all DELIVERED orders. */
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}

