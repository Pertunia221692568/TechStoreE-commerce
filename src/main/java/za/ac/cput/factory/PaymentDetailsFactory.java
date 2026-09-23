package za.ac.cput.factory;

import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.util.Helper;

public class PaymentDetailsFactory {
    public static PaymentDetails createPaymentDetails(String paymentMethod, String transactionId) {
        if (Helper.isNullOrEmpty(paymentMethod) || Helper.isNullOrEmpty(transactionId)) {
            return null;
        }
        String paymentId = Helper.generateId();
        return new PaymentDetails(paymentId, paymentMethod, transactionId);
    }
}
