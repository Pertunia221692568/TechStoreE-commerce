package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Products;
import za.ac.cput.domain.ShoppingCart;
import za.ac.cput.repository.IProductsRepository;
import za.ac.cput.repository.IShoppingCartRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class ShoppingCartService {

    /** What the frontend receives for each product in a cart. */
    public record CartLine(Products product, int quantity) {}

    @Autowired
    private IShoppingCartRepository repository;

    @Autowired
    private IProductsRepository productsRepository;

    public ShoppingCart save(ShoppingCart cart) {
        return repository.save(cart);
    }

    public Optional<ShoppingCart> findById(String id) {
        return repository.findById(id);
    }

    public List<ShoppingCart> findAll() {
        return repository.findAll();
    }

    public ShoppingCart update(ShoppingCart cart) {
        return repository.save(cart);
    }

    public boolean delete(ShoppingCart cart) {
        repository.delete(cart);
        return true;
    }

    // ── Cart contents (used by the website; no login needed) ─────────────

    /** READ: the cart's products (creates the cart the first time it is used). */
    @Transactional
    public List<CartLine> getCartItems(String cartId) {
        return saveAndList(cartFor(cartId));
    }

    /** CREATE: add a product (adds to the quantity if it is already in the cart). */
    @Transactional
    public List<CartLine> addItem(String cartId, Long productId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1");
        ShoppingCart cart = cartFor(cartId);
        Products product = findProduct(productId);

        int current = cart.getItems().getOrDefault(productId, 0);
        int newQty = Math.min(current + quantity, product.getStockQuantity());
        if (newQty < 1) throw new IllegalArgumentException(product.getName() + " is out of stock");

        cart.getItems().put(productId, newQty);
        return saveAndList(cart);
    }

    /** UPDATE: set the quantity of a product in the cart. */
    @Transactional
    public List<CartLine> setQuantity(String cartId, Long productId, int quantity) {
        ShoppingCart cart = cartFor(cartId);
        Products product = findProduct(productId);
        if (!cart.getItems().containsKey(productId)) {
            throw new IllegalArgumentException("Product is not in the cart");
        }
        cart.getItems().put(productId, Math.max(1, Math.min(quantity, product.getStockQuantity())));
        return saveAndList(cart);
    }

    /** DELETE: remove one product from the cart. */
    @Transactional
    public List<CartLine> removeItem(String cartId, Long productId) {
        ShoppingCart cart = cartFor(cartId);
        cart.getItems().remove(productId);
        return saveAndList(cart);
    }

    /** DELETE: empty the whole cart. */
    @Transactional
    public List<CartLine> clearCart(String cartId) {
        ShoppingCart cart = cartFor(cartId);
        cart.getItems().clear();
        return saveAndList(cart);
    }

    // ── helpers ──────────────────────────────────────────────

    private ShoppingCart cartFor(String cartId) {
        return repository.findById(cartId).orElseGet(() -> {
            LocalDateTime now = LocalDateTime.now();
            return repository.save(new ShoppingCart(cartId, now, now, 0));
        });
    }

    private Products findProduct(Long productId) {
        return productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }

    /** Drops products that no longer exist, updates totals, saves, and returns the lines. */
    private List<CartLine> saveAndList(ShoppingCart cart) {
        List<CartLine> lines = new ArrayList<>();
        List<Long> missing = new ArrayList<>();

        for (Map.Entry<Long, Integer> e : new TreeMap<>(cart.getItems()).entrySet()) {
            productsRepository.findById(e.getKey()).ifPresentOrElse(
                    p -> lines.add(new CartLine(p, e.getValue())),
                    () -> missing.add(e.getKey()));
        }
        missing.forEach(cart.getItems()::remove);

        cart.setTotalItems(lines.stream().mapToInt(CartLine::quantity).sum());
        cart.setLastUpdated(LocalDateTime.now());
        repository.save(cart);
        return lines;
    }
}
