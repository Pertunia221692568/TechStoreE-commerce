package za.ac.cput.controller;

import java.util.List;
import za.ac.cput.domain.Registration;
import za.ac.cput.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Registration> create(@RequestBody Registration registration) {
        return ResponseEntity.ok(service.create(registration));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> get(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Registration>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<Registration> update(@RequestBody Registration registration) {
        return ResponseEntity.ok(service.update(registration));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
