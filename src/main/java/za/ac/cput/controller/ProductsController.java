/* ProductController.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import za.ac.cput.domain.Products;
import za.ac.cput.service.ProductsService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
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
                    body.getOrDefault("category", "General").toString(),
                    str(body.get("imageUrl")),
                    str(body.get("itemCondition"))
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
                    body.getOrDefault("category", "General").toString(),
                    str(body.get("imageUrl")),
                    str(body.get("itemCondition"))
            );
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/products/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            // the product is part of an existing order
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error",
                    "This product is part of existing orders and cannot be deleted. Set its stock to 0 instead."));
        }
    }

    /** POST /api/products/upload — saves a product picture and returns its URL */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No file selected"));
            }
            String type = file.getContentType();
            if (type == null || !type.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }
            String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
            String ext = original.contains(".")
                    ? original.substring(original.lastIndexOf('.')).toLowerCase() : ".jpg";
            if (!ext.matches("\\.(jpg|jpeg|png|webp|gif)")) ext = ".jpg";

            Path dir = Paths.get("uploads", "products");
            Files.createDirectories(dir);
            String fileName = UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(Map.of("imageUrl", "/uploads/products/" + fileName));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Could not save the image"));
        }
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }
}
