package za.ac.cput.controller;

import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.service.PaymentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payment-details")
public class PaymentDetailsController {

    @Autowired
    private PaymentDetailsService service;

    @PostMapping("/create")
    public PaymentDetails create(@RequestBody PaymentDetails payment) {
        return service.save(payment);
    }

    @GetMapping("/getall")
    public List<PaymentDetails> getAll() {
        return service.findAll();
    }

    @PostMapping("/update")
    public PaymentDetails update(@RequestBody PaymentDetails payment) {
        return service.update(payment);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody PaymentDetails payment) {
        return service.delete(payment);
    }
}
