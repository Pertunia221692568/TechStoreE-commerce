package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.PaymentDetails;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentDetailsFactoryTest {

    @Test
    void testCreatePaymentDetails() {
        PaymentDetails payment = PaymentDetailsFactory.createPaymentDetails("Credit Card", "TXN123456789");
        assertNotNull(payment);
        assertNotNull(payment.getPaymentId());
        assertEquals("Credit Card", payment.getPaymentMethod());
        assertEquals("TXN123456789", payment.getTransactionId());
        assertEquals("PENDING", payment.getStatus());
    }

    @Test
    void testCreateCardPayment() {
        PaymentDetails payment = PaymentDetailsFactory.createCardPayment(
                "4111111111114242",
                "Jane Doe",
                "12/28",
                "123",
                new BigDecimal("999.00"),
                5L
        );
        assertNotNull(payment);
        assertEquals("CARD", payment.getPaymentMethod());
        assertEquals("4242", payment.getCardLast4());
        assertTrue(payment.getCardNumberMasked().contains("4242"));
        assertEquals("Jane Doe", payment.getCardHolderName());
        assertEquals("COMPLETED", payment.getStatus());
        assertEquals(5L, payment.getOrderId());
    }

    @Test
    void testCreatePaymentDetailsFail() {
        PaymentDetails payment = PaymentDetailsFactory.createPaymentDetails("", "TXN123456789");
        assertNull(payment);
    }
}