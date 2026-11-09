package za.ac.cput.service;

import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.factory.PaymentDetailsFactory;
import za.ac.cput.repository.IPaymentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentDetailsService {

    @Autowired
    private IPaymentDetailsRepository repository;

    public PaymentDetails save(PaymentDetails payment) {
        if (payment.getPaidAt() == null) payment.setPaidAt(java.time.LocalDateTime.now());
        return repository.save(payment);
    }

    public PaymentDetails createCardPayment(
            String cardNumber,
            String cardHolderName,
            String cardExpiry,
            String cvv,
            BigDecimal amount,
            Long orderId
    ) {
        PaymentDetails payment = PaymentDetailsFactory.createCardPayment(
                cardNumber, cardHolderName, cardExpiry, cvv, amount, orderId
        );
        if (payment == null) {
            throw new IllegalArgumentException("Invalid card payment details");
        }
        return repository.save(payment);
    }

    public Optional<PaymentDetails> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<PaymentDetails> findByPaymentId(String paymentId) {
        return repository.findByPaymentId(paymentId);
    }

    public List<PaymentDetails> findAll() {
        return repository.findAll();
    }

    public PaymentDetails update(PaymentDetails payment) {
        return repository.save(payment);
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}