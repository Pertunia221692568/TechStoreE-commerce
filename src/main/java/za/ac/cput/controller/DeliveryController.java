package za.ac.cput.controller;

import za.ac.cput.domain.Delivery;
import za.ac.cput.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/delivery", "/delivery"})
public class DeliveryController {

    @Autowired
    private DeliveryService service;

    @PostMapping("/create")
    public Delivery create(@RequestBody Delivery delivery) {
        return service.save(delivery);
    }

    @GetMapping("/getall")
    public List<Delivery> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-shipping-id/{shippingId}")
    public ResponseEntity<Delivery> getByShippingId(@PathVariable String shippingId) {
        return service.findByShippingId(shippingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update")
    public Delivery update(@RequestBody Delivery delivery) {
        return service.update(delivery);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return service.delete(id);
    }

    /**
     * GET /api/delivery/capacity/today
     * Returns how many product units are confirmed for dispatch today
     * and how much capacity remains.
     */
    @GetMapping("/capacity/today")
    public ResponseEntity<Map<String, Object>> todayCapacity() {
        DeliveryService.CapacityInfo info = service.getTodayCapacity();
        return ResponseEntity.ok(Map.of(
                "dailyLimit", info.dailyLimit(),
                "used", info.used(),
                "remaining", info.remaining(),
                "message", info.used() + " / " + info.dailyLimit() + " product units confirmed for dispatch today"
        ));
    }
}