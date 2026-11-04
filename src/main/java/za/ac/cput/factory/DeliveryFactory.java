package za.ac.cput.factory;

import za.ac.cput.domain.Delivery;
import za.ac.cput.util.Helper;

import java.time.LocalDateTime;

public class DeliveryFactory {

    public static Delivery createDelivery(String trackingNumber, String carrier, LocalDateTime estimatedDeliveryDate) {
        if (Helper.isNullOrEmpty(trackingNumber) || Helper.isNullOrEmpty(carrier)) {
            return null;
        }

        Delivery delivery = new Delivery();
        delivery.setShippingId("SHIP-TV-" + Helper.generateId());
        delivery.setTrackingNumber(trackingNumber);
        delivery.setCarrier(carrier);
        delivery.setEstimatedDeliveryDate(estimatedDeliveryDate);
        delivery.setStatus("PENDING");
        delivery.setProductUnits(0);
        return delivery;
    }

    public static Delivery createDelivery(
            String trackingNumber,
            String carrier,
            LocalDateTime estimatedDeliveryDate,
            int productUnits,
            Long orderId
    ) {
        Delivery delivery = createDelivery(trackingNumber, carrier, estimatedDeliveryDate);
        if (delivery == null) return null;

        delivery.setProductUnits(productUnits);
        delivery.setOrderId(orderId);
        delivery.setStatus("CONFIRMED");
        return delivery;
    }
}
