package za.ac.cput.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_details")
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId;

    private String paymentMethod;
    private String transactionId;

    private String cardNumberMasked;   // e.g. **** **** **** 4242
    private String cardLast4;
    private String cardHolderName;
    private String cardExpiry;         // MM/YY

    @Transient
    private String cvv;                // accepted at checkout, not stored long-term

    private BigDecimal amount;
    private String status;             // PENDING, COMPLETED, FAILED
    private LocalDateTime paidAt;
    private Long orderId;

    public PaymentDetails() {}

    public Long getId() { return id; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getCardNumberMasked() { return cardNumberMasked; }
    public void setCardNumberMasked(String cardNumberMasked) { this.cardNumberMasked = cardNumberMasked; }
    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}