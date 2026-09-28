package za.ac.cput.controller;

import java.util.List;
import za.ac.cput.domain.Login;
import za.ac.cput.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logins")
public class LoginController {

    private final LoginService service;

    public LoginController(LoginService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Login> create(@RequestBody Login login) {
        return ResponseEntity.ok(service.create(login));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Login> get(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Login>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<Login> update(@RequestBody Login login) {
        return ResponseEntity.ok(service.update(login));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}