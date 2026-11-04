package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shippingId;          // e.g. SHIP-TV-20261102-0042

    private String trackingNumber;
    private String carrier;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String status;              // PENDING, CONFIRMED, DISPATCHED, IN_TRANSIT, DELIVERED
    private int productUnits;           // used for daily capacity
    private Long orderId;

    public Delivery() {}

    public Long getId() { return id; }
    public String getShippingId() { return shippingId; }
    public void setShippingId(String shippingId) { this.shippingId = shippingId; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public LocalDateTime getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getProductUnits() { return productUnits; }
    public void setProductUnits(int productUnits) { this.productUnits = productUnits; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}