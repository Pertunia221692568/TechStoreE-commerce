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
    private final IDeliveryRepository  deliveryRepository;

    public OrderService(IOrderRepository orderRepository,
                        ICustomerRepository customerRepository,
                        IProductsRepository productRepository,
                        IDeliveryRepository deliveryRepository) {
        this.orderRepository    = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository  = productRepository;
        this.deliveryRepository = deliveryRepository;
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

        // 1. Validation: Ensure lists are not empty and sizes match
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product.");
        }
        if (quantities == null || productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Product IDs and quantities must match in size.");
        }

        // 2. Find customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + customerId));

        // 3. Create order using factory
        Order order = OrderFactory.create(customer, shippingAddress);

        // 4. Add each product as an order item
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

    /** Updates the status of an order.
     *  When status becomes SHIPPED or DELIVERED, a row is also saved in the deliveries table.
     */
    public Order updateStatus(Long orderId, Order.Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId));
        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);

        // SHIPPED / DELIVERED → create or update a delivery row
        if (newStatus == Order.Status.SHIPPED || newStatus == Order.Status.DELIVERED) {
            String shippingId = "SHIP-TV-ORDER-" + orderId;

            Delivery delivery = deliveryRepository.findByShippingId(shippingId)
                    .orElseGet(() -> {
                        Delivery d = new Delivery();
                        d.setShippingId(shippingId);
                        d.setTrackingNumber("TV-" + orderId + "-" + (System.currentTimeMillis() % 100000));
                        d.setCarrier("TechVault Courier");
                        d.setOrderId(orderId);
                        d.setProductUnits(0);
                        d.setStatus("PENDING");
                        return d;
                    });

            if (newStatus == Order.Status.SHIPPED) {
                delivery.setStatus("DISPATCHED");
                delivery.setEstimatedDeliveryDate(java.time.LocalDateTime.now().plusDays(2));
                delivery.setActualDeliveryDate(null);
            } else {
                // DELIVERED
                delivery.setStatus("DELIVERED");
                delivery.setActualDeliveryDate(java.time.LocalDateTime.now());
            }

            deliveryRepository.save(delivery);
        }

        return saved;
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