package za.ac.cput.factory;

import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.util.Helper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDetailsFactory {

    public static PaymentDetails createPaymentDetails(String paymentMethod, String transactionId) {
        if (Helper.isNullOrEmpty(paymentMethod) || Helper.isNullOrEmpty(transactionId)) {
            return null;
        }

        PaymentDetails payment = new PaymentDetails();
        payment.setPaymentId(Helper.generateId());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        payment.setStatus("PENDING");
        payment.setPaidAt(LocalDateTime.now());
        return payment;
    }

    /** Create a card payment with masked card details */
    public static PaymentDetails createCardPayment(
            String cardNumber,
            String cardHolderName,
            String cardExpiry,
            String cvv,
            BigDecimal amount,
            Long orderId
    ) {
        if (Helper.isNullOrEmpty(cardNumber) || Helper.isNullOrEmpty(cardHolderName)
                || Helper.isNullOrEmpty(cardExpiry) || amount == null) {
            return null;
        }

        String cleaned = cardNumber.replaceAll("\\s+", "");
        String last4 = cleaned.length() >= 4 ? cleaned.substring(cleaned.length() - 4) : cleaned;
        String masked = "**** **** **** " + last4;

        PaymentDetails payment = new PaymentDetails();
        payment.setPaymentId("PAY-" + Helper.generateId());
        payment.setPaymentMethod("CARD");
        payment.setTransactionId("TXN-" + Helper.generateId());
        payment.setCardNumberMasked(masked);
        payment.setCardLast4(last4);
        payment.setCardHolderName(cardHolderName);
        payment.setCardExpiry(cardExpiry);
        payment.setCvv(cvv);                 // transient – not stored long-term
        payment.setAmount(amount);
        payment.setStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());
        payment.setOrderId(orderId);
        return payment;
    }
}