package za.ac.cput.service;

import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.repository.IPaymentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentDetailsService {

    @Autowired
    private IPaymentDetailsRepository repository;

    public PaymentDetails save(PaymentDetails payment) {
        return repository.save(payment);
    }

    public Optional<PaymentDetails> findById(String id) {
        return repository.findById(id);
    }

    public List<PaymentDetails> findAll() {
        return repository.findAll();
    }

    public PaymentDetails update(PaymentDetails payment) {
        return repository.save(payment);
    }

    public boolean delete(PaymentDetails payment) {
        repository.delete(payment);
        return true;
    }
}