/* ProductController.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import za.ac.cput.domain.Products;
import za.ac.cput.service.ProductsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ProductController – REST Controller
 * Base URL: http://localhost:8080/api/products
 */
@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductsService productService;

    public ProductsController(ProductsService productService) {
        this.productService = productService;
    }

    /** GET /api/products — get all products, optional search or category filter */
    @GetMapping
    public ResponseEntity<List<Products>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        if (search != null)   return ResponseEntity.ok(productService.search(search));
        if (category != null) return ResponseEntity.ok(productService.findByCategory(category));
        return ResponseEntity.ok(productService.findAll());
    }

    /** GET /api/products/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<Products> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/products/in-stock */
    @GetMapping("/in-stock")
    public ResponseEntity<List<Products>> getInStock() {
        return ResponseEntity.ok(productService.findInStock());
    }

    /** POST /api/products */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Products product = productService.create(
                    body.get("name").toString(),
                    body.get("brand").toString(),
                    body.getOrDefault("description", "").toString(),
                    new BigDecimal(body.get("price").toString()),
                    Integer.parseInt(body.get("stockQuantity").toString()),
                    body.getOrDefault("category", "General").toString()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/products/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body) {
        try {
            Products product = productService.update(
                    id,
                    body.get("name").toString(),
                    body.get("brand").toString(),
                    body.getOrDefault("description", "").toString(),
                    new BigDecimal(body.get("price").toString()),
                    Integer.parseInt(body.get("stockQuantity").toString()),
                    body.getOrDefault("category", "General").toString()
            );
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/products/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

