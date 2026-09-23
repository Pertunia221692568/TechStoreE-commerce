/* Product.java class
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * This class Represents a tech product (laptop, accessory, etc.)
 * sold on our TechStore platform.
 */
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private String category; // e.g. Laptop, Mouse, Keyboard, Monitor

    protected Products() {}


    private Products(Builder builder) {
        this.name          = builder.name;
        this.brand         = builder.brand;
        this.description   = builder.description;
        this.price         = builder.price;
        this.stockQuantity = builder.stockQuantity;
        this.category      = builder.category;
    }

    //Getters
    public Long getId()            { return id; }
    public String getName()        { return name; }
    public String getBrand()       { return brand; }
    public String getDescription() { return description; }
    public BigDecimal getPrice()   { return price; }
    public int getStockQuantity()  { return stockQuantity; }
    public String getCategory()    { return category; }
    public boolean isInStock()     { return stockQuantity > 0; }

    // Setters
    public void setName(String name)               { this.name = name; }
    public void setBrand(String brand)             { this.brand = brand; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price)         { this.price = price; }
    public void setStockQuantity(int qty)          { this.stockQuantity = qty; }
    public void setCategory(String category)       { this.category = category; }

    /**
     * Reduces stock when an order is placed.
     * it Throws an exception if there is not enough stock.
     */
    public void reduceStock(int quantity) {
        if (quantity > this.stockQuantity) {
            throw new IllegalArgumentException(
                    "Not enough stock for product: " + name +
                            ". Available: " + stockQuantity + ", Requested: " + quantity);
        }
        this.stockQuantity -= quantity;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', brand='" + brand +
                "', price=" + price + ", stock=" + stockQuantity + "}";
    }

    //  Builder
    public static class Builder {
        private String name;
        private String brand;
        private String description;
        private BigDecimal price;
        private int stockQuantity;
        private String category;

        public Builder name(String name)               { this.name = name; return this; }
        public Builder brand(String brand)             { this.brand = brand; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder price(BigDecimal price)         { this.price = price; return this; }
        public Builder stockQuantity(int qty)          { this.stockQuantity = qty; return this; }
        public Builder category(String category)       { this.category = category; return this; }

        public Products build() {
            if (name == null || name.isBlank())
                throw new IllegalStateException("Product name is required");
            if (brand == null || brand.isBlank())
                throw new IllegalStateException("Product brand is required");
            if (price == null)
                throw new IllegalStateException("Product price is required");
            if (price.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalStateException("Price cannot be negative");
            if (stockQuantity < 0)
                throw new IllegalStateException("Stock quantity cannot be negative");
            return new Products(this);
        }
    }
}
