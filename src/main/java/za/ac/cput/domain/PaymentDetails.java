package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_details")
public class PaymentDetails {

    @Id
    private String paymentId;
    private String paymentMethod;
    private String transactionId;

    public PaymentDetails() {}

    public PaymentDetails(String paymentId, String paymentMethod, String transactionId) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
