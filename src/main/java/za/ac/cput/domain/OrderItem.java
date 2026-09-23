/* OrderItem.java class
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    protected OrderItem() {}

    private OrderItem(Builder builder) {
        this.order     = builder.order;
        this.product   = builder.product;
        this.quantity  = builder.quantity;
        this.unitPrice = builder.unitPrice;
    }

    // Getters
    public Long getId()              { return id; }
    public Order getOrder()          { return order; }
    public Products getProduct()      { return product; }
    public int getQuantity()         { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    /** Calculates subtotal: unitPrice × quantity */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    //  Setters
    public void setOrder(Order order)       { this.order = order; }
    public void setQuantity(int quantity)   { this.quantity = quantity; }

    @Override
    public String toString() {
        return "OrderItem{id=" + id + ", product=" + product.getName() +
                ", qty=" + quantity + ", unitPrice=R" + unitPrice + "}";
    }

    // Builder
    public static class Builder {
        private Order order;
        private Products product;
        private int quantity;
        private BigDecimal unitPrice;

        public Builder order(Order order)          { this.order = order; return this; }
        public Builder product(Products product)    { this.product = product; return this; }
        public Builder quantity(int quantity)      { this.quantity = quantity; return this; }
        public Builder unitPrice(BigDecimal price) { this.unitPrice = price; return this; }

        public OrderItem build() {
            if (product == null)
                throw new IllegalStateException("Product is required for an OrderItem");
            if (quantity <= 0)
                throw new IllegalStateException("Quantity must be greater than zero");
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalStateException("Unit price must be zero or greater");
            return new OrderItem(this);
        }
    }
}
