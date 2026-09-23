package za.ac.cput.factory;

import za.ac.cput.domain.Delivery;
import za.ac.cput.util.Helper;
import java.time.LocalDateTime;

public class DeliveryFactory {
    public static Delivery createDelivery(String trackingNumber, String carrier, LocalDateTime deliveryDate) {
        if (Helper.isNullOrEmpty(trackingNumber) || Helper.isNullOrEmpty(carrier)) {
            return null;
        }
        String deliveryId = Helper.generateId();
        return new Delivery(deliveryId, trackingNumber, carrier, deliveryDate);
    }
}
