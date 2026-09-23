package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.PaymentDetails;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentDetailsFactoryTest {

    @Test
    void testCreatePaymentDetails() {
        PaymentDetails payment = PaymentDetailsFactory.createPaymentDetails("Credit Card", "TXN123456789");
        assertNotNull(payment);
        assertNotNull(payment.getPaymentId());
        assertEquals("Credit Card", payment.getPaymentMethod());
        assertEquals("TXN123456789", payment.getTransactionId());
    }

    @Test
    void testCreatePaymentDetailsFail() {
        PaymentDetails payment = PaymentDetailsFactory.createPaymentDetails("", "TXN123456789");
        assertNull(payment);
    }
}
