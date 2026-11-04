package za.ac.cput.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.Delivery;
import za.ac.cput.repository.IDeliveryRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private IDeliveryRepository repository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Delivery delivery;

    @BeforeEach
    void setUp() {
        delivery = new Delivery();
        delivery.setShippingId("SHIP-TV-001");
        delivery.setTrackingNumber("TV-12345");
        delivery.setCarrier("TechVault Courier");
        delivery.setStatus("PENDING");
        delivery.setProductUnits(2);
        delivery.setOrderId(1L);
        delivery.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testSaveDelivery() {
        when(repository.save(any(Delivery.class))).thenReturn(delivery);

        Delivery saved = deliveryService.save(delivery);

        assertNotNull(saved);
        assertEquals("SHIP-TV-001", saved.getShippingId());
        verify(repository, times(1)).save(delivery);
    }

    @Test
    void testFindDeliveryById() {
        when(repository.findById(1L)).thenReturn(Optional.of(delivery));

        Optional<Delivery> found = deliveryService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("SHIP-TV-001", found.get().getShippingId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindByShippingId() {
        when(repository.findByShippingId("SHIP-TV-001")).thenReturn(Optional.of(delivery));

        Optional<Delivery> found = deliveryService.findByShippingId("SHIP-TV-001");

        assertTrue(found.isPresent());
        assertEquals("TV-12345", found.get().getTrackingNumber());
        verify(repository, times(1)).findByShippingId("SHIP-TV-001");
    }
}