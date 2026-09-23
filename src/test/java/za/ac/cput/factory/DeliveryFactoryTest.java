package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Delivery;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class DeliveryFactoryTest {

    @Test
    void testCreateDelivery() {
        LocalDateTime deliveryDate = LocalDateTime.now().plusDays(3);
        Delivery delivery = DeliveryFactory.createDelivery("TRK987654", "DHL", deliveryDate);
        assertNotNull(delivery);
        assertNotNull(delivery.getDeliveryId());
        assertEquals("TRK987654", delivery.getTrackingNumber());
        assertEquals("DHL", delivery.getCarrier());
    }

    @Test
    void testCreateDeliveryFail() {
        Delivery delivery = DeliveryFactory.createDelivery("", "DHL", LocalDateTime.now());
        assertNull(delivery);
    }
}
