package za.ac.cput.domain;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {

    @Id
    private String cartId;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private Integer totalItems;

    /**
     * The products in this cart: productId -> quantity.
     * Stored in its own table (shopping_cart_items) that belongs to the cart,
     * so no extra entity class is needed.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shopping_cart_items", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Long, Integer> items = new HashMap<>();

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

    public Map<Long, Integer> getItems() { return items; }
    public void setItems(Map<Long, Integer> items) { this.items = items; }
}
