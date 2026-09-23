package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {

    @Id
    private String cartId;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private Integer totalItems;

    public ShoppingCart() {}

    public ShoppingCart(String cartId, LocalDateTime createdAt, LocalDateTime lastUpdated, Integer totalItems) {
        this.cartId = cartId;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.totalItems = totalItems;
    }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}

