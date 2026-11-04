package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Delivery;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryFactoryTest {

    @Test
    void testCreateDelivery() {
        LocalDateTime estimatedDate = LocalDateTime.now().plusDays(3);
        Delivery delivery = DeliveryFactory.createDelivery("TRK987654", "DHL", estimatedDate);

        assertNotNull(delivery);
        assertNotNull(delivery.getShippingId());
        assertTrue(delivery.getShippingId().startsWith("SHIP-TV-"));
        assertEquals("TRK987654", delivery.getTrackingNumber());
        assertEquals("DHL", delivery.getCarrier());
        assertEquals("PENDING", delivery.getStatus());
        assertEquals(estimatedDate, delivery.getEstimatedDeliveryDate());
    }

    @Test
    void testCreateDeliveryWithOrder() {
        LocalDateTime estimatedDate = LocalDateTime.now().plusDays(2);
        Delivery delivery = DeliveryFactory.createDelivery(
                "TRK111222", "TechVault Courier", estimatedDate, 3, 10L
        );

        assertNotNull(delivery);
        assertNotNull(delivery.getShippingId());
        assertEquals("TRK111222", delivery.getTrackingNumber());
        assertEquals(3, delivery.getProductUnits());
        assertEquals(10L, delivery.getOrderId());
        assertEquals("CONFIRMED", delivery.getStatus());
    }

    @Test
    void testCreateDeliveryFail() {
        Delivery delivery = DeliveryFactory.createDelivery("", "DHL", LocalDateTime.now());
        assertNull(delivery);
    }
}
