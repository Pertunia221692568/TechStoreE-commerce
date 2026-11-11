/* AdminController.java
 * Admin-only API (secured in SecurityConfig with hasRole ADMIN):
 * dashboard stats, customers, payments, deliveries and enquiries.
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.*;
import za.ac.cput.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Transactional(readOnly = true)
public class AdminController {

    private static final int LOW_STOCK = 5;

    private final IOrderRepository orderRepository;
    private final IUserRepository userRepository;
    private final ICustomerRepository customerRepository;
    private final IProductsRepository productsRepository;
    private final IPaymentDetailsRepository paymentRepository;
    private final IDeliveryRepository deliveryRepository;
    private final IEnquiryRepository enquiryRepository;

    public AdminController(IOrderRepository orderRepository, IUserRepository userRepository,
                           ICustomerRepository customerRepository, IProductsRepository productsRepository,
                           IPaymentDetailsRepository paymentRepository, IDeliveryRepository deliveryRepository,
                           IEnquiryRepository enquiryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.productsRepository = productsRepository;
        this.paymentRepository = paymentRepository;
        this.deliveryRepository = deliveryRepository;
        this.enquiryRepository = enquiryRepository;
    }

    // ---------------------------------------------------------------- stats
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<Order> orders = orderRepository.findAll();
        List<Order> active = orders.stream().filter(o -> o.getStatus() != Order.Status.CANCELLED).toList();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Order.Status s : Order.Status.values()) {
            byStatus.put(s.name(), orders.stream().filter(o -> o.getStatus() == s).count());
        }

        BigDecimal revenue = orders.stream().filter(o -> o.getStatus() == Order.Status.DELIVERED)
                .map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pipeline = orders.stream()
                .filter(o -> o.getStatus() != Order.Status.DELIVERED && o.getStatus() != Order.Status.CANCELLED)
                .map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // last 7 days
        List<Map<String, Object>> last7 = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            List<Order> dayOrders = active.stream()
                    .filter(o -> o.getOrderDate() != null && o.getOrderDate().toLocalDate().equals(day)).toList();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", day.toString());
            row.put("orders", dayOrders.size());
            row.put("revenue", dayOrders.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            last7.add(row);
        }

        // top selling products
        Map<String, int[]> units = new HashMap<>();
        Map<String, BigDecimal> money = new HashMap<>();
        for (Order o : active) {
            for (OrderItem it : o.getOrderItems()) {
                String name = it.getProduct() != null ? it.getProduct().getName() : "Unknown";
                units.computeIfAbsent(name, k -> new int[1])[0] += it.getQuantity();
                money.merge(name, it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())), BigDecimal::add);
            }
        }
        List<Map<String, Object>> top = units.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0])).limit(5)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("units", e.getValue()[0]);
                    m.put("revenue", money.get(e.getKey()));
                    return m;
                }).collect(Collectors.toList());

        List<Products> products = productsRepository.findAll();
        List<Map<String, Object>> lowStock = products.stream()
                .filter(p -> p.getStockQuantity() <= LOW_STOCK)
                .sorted(Comparator.comparingInt(Products::getStockQuantity)).limit(8)
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", p.getId());
                    m.put("name", p.getName());
                    m.put("stock", p.getStockQuantity());
                    m.put("imageUrl", p.getImageUrl());
                    return m;
                }).collect(Collectors.toList());

        List<Map<String, Object>> recent = orders.stream()
                .sorted(Comparator.comparing(Order::getOrderDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(6).map(this::orderRow).collect(Collectors.toList());

        long customers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER && !isDeleted(u)).count();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalOrders", orders.size());
        out.put("revenue", revenue);
        out.put("pipelineValue", pipeline);
        out.put("customers", customers);
        out.put("products", products.size());
        out.put("lowStockCount", products.stream().filter(p -> p.getStockQuantity() <= LOW_STOCK).count());
        out.put("openEnquiries", enquiryRepository.countByStatus("NEW"));
        out.put("ordersByStatus", byStatus);
        out.put("last7Days", last7);
        out.put("topProducts", top);
        out.put("lowStock", lowStock);
        out.put("recentOrders", recent);
        return out;
    }

    private Map<String, Object> orderRow(Order o) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", o.getId());
        m.put("customer", o.getCustomer() != null && o.getCustomer().getUser() != null
                ? o.getCustomer().getUser().getFullName() : "—");
        m.put("total", o.getTotalAmount());
        m.put("status", o.getStatus().name());
        m.put("date", o.getOrderDate());
        return m;
    }

    // ------------------------------------------------------------ customers
    @GetMapping("/customers")
    public List<Map<String, Object>> customers() {
        Map<Long, List<Order>> byCustomer = orderRepository.findAll().stream()
                .filter(o -> o.getCustomer() != null)
                .collect(Collectors.groupingBy(o -> o.getCustomer().getId()));

        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER && !isDeleted(u))
                .map(u -> {
                    Long cid = u.getCustomer() != null ? u.getCustomer().getId() : null;
                    List<Order> mine = cid == null ? List.of() : byCustomer.getOrDefault(cid, List.of());
                    BigDecimal spent = mine.stream().filter(o -> o.getStatus() != Order.Status.CANCELLED)
                            .map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("userId", u.getId());
                    m.put("customerId", cid);
                    m.put("name", u.getFullName());
                    m.put("email", u.getEmail());
                    m.put("phone", u.getPhone());
                    m.put("enabled", u.isEnabled());
                    m.put("createdAt", u.getCreatedAt());
                    m.put("lastLoginAt", u.getLastLoginAt());
                    m.put("orders", mine.size());
                    m.put("totalSpent", spent);
                    return m;
                })
                .sorted((a, b) -> String.valueOf(b.get("createdAt")).compareTo(String.valueOf(a.get("createdAt"))))
                .collect(Collectors.toList());
    }

    @PatchMapping("/customers/{userId}/enabled")
    @Transactional
    public ResponseEntity<?> setEnabled(@PathVariable Long userId, @RequestBody Map<String, Boolean> body) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        if (user.getRole() != Role.CUSTOMER) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only customer accounts can be changed here."));
        }
        user.setEnabled(Boolean.TRUE.equals(body.get("enabled")));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("enabled", user.isEnabled()));
    }

    private static boolean isDeleted(User u) {
        return u.getEmail() != null && u.getEmail().endsWith("@deleted.invalid");
    }

    // ------------------------------------------------------------- payments
    @GetMapping("/payments")
    public List<Map<String, Object>> payments() {
        return paymentRepository.findAll().stream()
                .sorted(Comparator.comparing(PaymentDetails::getId).reversed())
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", p.getId());
                    m.put("paymentId", p.getPaymentId());
                    m.put("orderId", p.getOrderId());
                    m.put("method", p.getPaymentMethod());
                    m.put("cardLast4", p.getCardLast4());
                    m.put("cardHolder", p.getCardHolderName());
                    m.put("amount", p.getAmount());
                    m.put("status", p.getStatus());
                    m.put("paidAt", p.getPaidAt());
                    return m;   // never expose full card details
                }).collect(Collectors.toList());
    }

    // ----------------------------------------------------------- deliveries
    @GetMapping("/deliveries")
    public List<Map<String, Object>> deliveries() {
        Map<Long, Order> orders = orderRepository.findAll().stream()
                .collect(Collectors.toMap(Order::getId, o -> o));
        return deliveryRepository.findAll().stream()
                .sorted(Comparator.comparing(Delivery::getId).reversed())
                .map(d -> {
                    Order o = orders.get(d.getOrderId());
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", d.getId());
                    m.put("orderId", d.getOrderId());
                    m.put("customer", o != null && o.getCustomer() != null && o.getCustomer().getUser() != null
                            ? o.getCustomer().getUser().getFullName() : "—");
                    m.put("address", o != null ? o.getShippingAddress() : null);
                    m.put("shippingId", d.getShippingId());
                    m.put("trackingNumber", d.getTrackingNumber());
                    m.put("carrier", d.getCarrier());
                    m.put("status", d.getStatus());
                    m.put("units", d.getProductUnits());
                    m.put("estimatedDeliveryDate", d.getEstimatedDeliveryDate());
                    m.put("actualDeliveryDate", d.getActualDeliveryDate());
                    return m;
                }).collect(Collectors.toList());
    }

    @PatchMapping("/deliveries/{id}")
    @Transactional
    public ResponseEntity<?> updateDelivery(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Delivery d = deliveryRepository.findById(id).orElse(null);
        if (d == null) return ResponseEntity.notFound().build();
        String carrier = body.getOrDefault("carrier", d.getCarrier()).trim();
        String tracking = body.getOrDefault("trackingNumber", d.getTrackingNumber()).trim();
        if (carrier.isEmpty() || carrier.length() > 60 || tracking.isEmpty() || tracking.length() > 60) {
            return ResponseEntity.badRequest().body(Map.of("error", "Courier and tracking number are required (max 60 characters)."));
        }
        d.setCarrier(carrier);
        d.setTrackingNumber(tracking);
        deliveryRepository.save(d);
        return ResponseEntity.ok(Map.of("carrier", carrier, "trackingNumber", tracking));
    }

    // ------------------------------------------------------------ enquiries
    @GetMapping("/enquiries")
    public List<Enquiry> enquiries() {
        return enquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    @PatchMapping("/enquiries/{id}")
    @Transactional
    public ResponseEntity<?> updateEnquiry(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Enquiry e = enquiryRepository.findById(id).orElse(null);
        if (e == null) return ResponseEntity.notFound().build();

        String status = body.get("status");
        if (status != null) {
            if (!List.of("NEW", "IN_PROGRESS", "RESOLVED").contains(status)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid status."));
            }
            e.setStatus(status);
        }
        String reply = body.get("adminReply");
        if (reply != null) {
            String r = reply.trim();
            if (r.length() > 2000) r = r.substring(0, 2000);
            e.setAdminReply(r.isEmpty() ? null : r);
            e.setRepliedAt(r.isEmpty() ? null : LocalDateTime.now());
        }
        return ResponseEntity.ok(enquiryRepository.save(e));
    }
}
