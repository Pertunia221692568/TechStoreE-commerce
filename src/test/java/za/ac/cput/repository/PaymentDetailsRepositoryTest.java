package za.ac.cput.repository;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.factory.PaymentDetailsFactory;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentDetailsRepositoryTest {

    private static final IPaymentDetailsRepository repository = ;

    @Test
    void testSave() {
        PaymentDetails payment = PaymentDetailsFactory.createPaymentDetails("PayPal", "TXN111222");
        PaymentDetails saved = repository.save(payment);
        assertNotNull(saved);
        assertEquals(payment.getPaymentId(), saved.getPaymentId());
    }

    @Test
    void testFindAll() {
        assertNotNull(repository.findAll());
    }
}
