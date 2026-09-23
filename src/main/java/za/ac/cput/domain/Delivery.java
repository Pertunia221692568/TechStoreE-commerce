package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    private String deliveryId;
    private String trackingNumber;
    private String carrier;
    private LocalDateTime deliveryDate;

    public Delivery() {}

    public Delivery(String deliveryId, String trackingNumber, String carrier, LocalDateTime deliveryDate) {
        this.deliveryId = deliveryId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }
}
