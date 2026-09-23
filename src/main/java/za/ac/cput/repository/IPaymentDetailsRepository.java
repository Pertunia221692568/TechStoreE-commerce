package za.ac.cput.repository;

import za.ac.cput.domain.PaymentDetails;
import java.util.List;

public interface IPaymentDetailsRepository {
    PaymentDetails save(PaymentDetails paymentDetails);
    List<PaymentDetails> findAll();
    PaymentDetails update(PaymentDetails paymentDetails);
    boolean delete(PaymentDetails paymentDetails);
}
