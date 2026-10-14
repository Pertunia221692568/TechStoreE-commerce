package za.ac.cput.controller;

import za.ac.cput.domain.ShoppingCart;
import za.ac.cput.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService service;

    @PostMapping("/create")
    public ShoppingCart create(@RequestBody ShoppingCart cart) {
        return service.save(cart);
    }

    @GetMapping("/getall")
    public List<ShoppingCart> getAll() {
        return service.findAll();
    }

    @PostMapping("/update")
    public ShoppingCart update(@RequestBody ShoppingCart cart) {
        return service.update(cart);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody ShoppingCart cart) {
        return service.delete(cart);
    }



    /** GET /shopping-cart/{cartId} — the products in the cart */
    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable String cartId) {
        return ResponseEntity.ok(service.getCartItems(cartId));
    }

    /** POST /shopping-cart/{cartId}/items — body: { productId, quantity } */
    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItem(@PathVariable String cartId, @RequestBody Map<String, Object> body) {
        try {
            Long productId = Long.valueOf(body.get("productId").toString());
            int quantity = Integer.parseInt(body.getOrDefault("quantity", 1).toString());
            return ResponseEntity.ok(service.addItem(cartId, productId, quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }


    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(@PathVariable String cartId, @PathVariable Long productId,
                                        @RequestBody Map<String, Object> body) {
        try {
            int quantity = Integer.parseInt(body.get("quantity").toString());
            return ResponseEntity.ok(service.setQuantity(cartId, productId, quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }


    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable String cartId, @PathVariable Long productId) {
        return ResponseEntity.ok(service.removeItem(cartId, productId));
    }


    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable String cartId) {
        return ResponseEntity.ok(service.clearCart(cartId));
    }
}
