package za.ac.cput.service;

import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.repository.IPaymentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentDetailsService {

    @Autowired
    private IPaymentDetailsRepository repository;

    public PaymentDetails save(PaymentDetails payment) {
        return repository.save(payment);
    }

    public List<PaymentDetails> findAll() {
        return repository.findAll();
    }

    public PaymentDetails update(PaymentDetails payment) {
        return repository.save(payment);   // use save() for update
    }

    public boolean delete(PaymentDetails payment) {
        repository.delete(payment);        // delete() returns void
        return true;
    }
}