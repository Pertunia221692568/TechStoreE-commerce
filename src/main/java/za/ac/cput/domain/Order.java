/* TechStoreE-commerce – ADP372S
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    public enum Status {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    private String shippingAddress;
    private String notes;

    protected Order() {}

    private Order(Builder builder) {
        this.customer        = builder.customer;
        this.orderDate       = LocalDateTime.now();
        this.status          = Status.PENDING;
        this.totalAmount     = BigDecimal.ZERO;
        this.shippingAddress = builder.shippingAddress;
        this.notes           = builder.notes;
    }

    // Getters
    public Long getId()                    { return id; }
    public Customer getCustomer()          { return customer; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public LocalDateTime getOrderDate()    { return orderDate; }
    public Status getStatus()              { return status; }
    public BigDecimal getTotalAmount()     { return totalAmount; }
    public String getShippingAddress()     { return shippingAddress; }
    public String getNotes()               { return notes; }

    // Setters
    public void setStatus(Status status)               { this.status = status; }
    public void setShippingAddress(String address)     { this.shippingAddress = address; }
    public void setNotes(String notes)                 { this.notes = notes; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }


    /** Adds an item to the order and recalculates the total. */
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    /** Recalculates the total from all order items. */
    public void recalculateTotal() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** An order can only be cancelled if it is PENDING or CONFIRMED. */
    public boolean isCancellable() {
        return status == Status.PENDING || status == Status.CONFIRMED;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", customer=" + customer.getFullName() +
                ", status=" + status + ", total=R" + totalAmount + "}";
    }

    // ── Builder ───────────────────────────────────────────────
    public static class Builder {
        private Customer customer;
        private String shippingAddress;
        private String notes;

        public Builder customer(Customer customer)         { this.customer = customer; return this; }
        public Builder shippingAddress(String address)     { this.shippingAddress = address; return this; }
        public Builder notes(String notes)                 { this.notes = notes; return this; }

        public Order build() {
            if (customer == null)
                throw new IllegalStateException("Customer is required to create an Order");
            return new Order(this);
        }
    }
}
