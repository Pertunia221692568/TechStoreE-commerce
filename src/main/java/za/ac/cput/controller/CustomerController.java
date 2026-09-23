/* CustomerController.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import za.ac.cput.domain.Customer;
import za.ac.cput.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CustomerController – REST Controller
 * Base URL: http://localhost:8080/api/customers
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            Customer customer = customerService.register(
                    body.get("firstName"),
                    body.get("lastName"),
                    body.get("email"),
                    body.getOrDefault("phone", ""),
                    body.getOrDefault("address", "")
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Map<String, String> body) {
        try {
            Customer customer = customerService.update(
                    id,
                    body.get("firstName"),
                    body.get("lastName"),
                    body.get("email"),
                    body.getOrDefault("phone", ""),
                    body.getOrDefault("address", "")
            );
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

