package za.ac.cput.repository;

import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.factory.PaymentDetailsFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentDetailsRepositoryTest {

    @Autowired
    private IPaymentDetailsRepository repository;

    @Test
    void testSave() {
        PaymentDetails payment = PaymentDetailsFactory.createPaymentDetails("PayPal", "transaction123");

        assertNotNull(payment);

        PaymentDetails saved = repository.save(payment);

        assertNotNull(saved);
        assertNotNull(saved.getPaymentId());
        assertEquals("PayPal", saved.getPaymentMethod());
        assertEquals("transaction123", saved.getTransactionId());
    }

    @Test
    void testFindAll() {
        assertNotNull(repository.findAll());
    }
}