package za.ac.cput.controller;

import za.ac.cput.domain.Delivery;
import za.ac.cput.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/delivery")
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

    @PostMapping("/update")
    public Delivery update(@RequestBody Delivery delivery) {
        return service.update(delivery);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody Delivery delivery) {
        return service.delete(delivery);
    }
}
