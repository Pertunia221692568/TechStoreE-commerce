package za.ac.cput.controller;

import java.util.List;
import java.util.Map;
import za.ac.cput.domain.Rating;
import za.ac.cput.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Rating rating) {
        try {
            return ResponseEntity.ok(service.create(rating));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> get(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** All ratings for one product */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getByProductId(productId));
    }

    @GetMapping
    public ResponseEntity<List<Rating>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<Rating> update(@RequestBody Rating rating) {
        return ResponseEntity.ok(service.update(rating));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}