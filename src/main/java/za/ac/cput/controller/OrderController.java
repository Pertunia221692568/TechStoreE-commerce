/* OrderController.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import za.ac.cput.domain.Order;
import za.ac.cput.service.OrderService;
import za.ac.cput.util.OrderUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Base URL: http://localhost:8080/api/orders
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** GET /api/orders — get all orders, optionally filter by status */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            try {
                Order.Status s = Order.Status.valueOf(status.toUpperCase());
                return ResponseEntity.ok(orderService.findByStatus(s));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(orderService.findAll());
    }

    /** GET /api/orders/{id} — get one order by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/orders/customer/{customerId} — get orders by customer */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.findByCustomer(customerId));
    }

    /**
     * POST /api/orders — place a new order
     * Body: { customerId, shippingAddress, productIds: [], quantities: [] }
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body) {
        try {
            Long customerId       = Long.valueOf(body.get("customerId").toString());
            String shippingAddress = body.get("shippingAddress").toString();

            @SuppressWarnings("unchecked")
            List<Integer> productIdInts = (List<Integer>) body.get("productIds");
            @SuppressWarnings("unchecked")
            List<Integer> quantityInts  = (List<Integer>) body.get("quantities");

            List<Long>    productIds = productIdInts.stream().map(Long::valueOf).toList();
            List<Integer> quantities = quantityInts;

            Order order = orderService.placeOrder(customerId, shippingAddress, productIds, quantities);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/orders/{id}/status — update order status */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body) {
        try {
            Order.Status newStatus = Order.Status.valueOf(body.get("status").toUpperCase());
            Order updated = orderService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/orders/{id}/cancel — cancel an order */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            Order cancelled = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/orders/{id} — delete an order */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/orders/stats — dashboard stats */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalOrders",     orderService.findAll().size(),
                "pendingOrders",   orderService.countByStatus(Order.Status.PENDING),
                "confirmedOrders", orderService.countByStatus(Order.Status.CONFIRMED),
                "shippedOrders",   orderService.countByStatus(Order.Status.SHIPPED),
                "deliveredOrders", orderService.countByStatus(Order.Status.DELIVERED),
                "cancelledOrders", orderService.countByStatus(Order.Status.CANCELLED),
                "totalRevenue",    orderService.getTotalRevenue()
        ));
    }
}

