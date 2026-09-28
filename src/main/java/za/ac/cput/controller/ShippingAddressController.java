package za.ac.cput.controller;

import java.util.List;
import za.ac.cput.domain.ShippingAddress;
import za.ac.cput.service.ShippingAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping-addresses")
public class ShippingAddressController {

    private final ShippingAddressService service;

    public ShippingAddressController(ShippingAddressService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShippingAddress> create(@RequestBody ShippingAddress address) {
        return ResponseEntity.ok(service.create(address));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingAddress> get(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ShippingAddress>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<ShippingAddress> update(@RequestBody ShippingAddress address) {
        return ResponseEntity.ok(service.update(address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
