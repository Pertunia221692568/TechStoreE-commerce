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
        return repository.update(payment);
    }

    public boolean delete(PaymentDetails payment) {
        return repository.delete(payment);
    }
}
