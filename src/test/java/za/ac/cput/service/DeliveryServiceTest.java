package za.ac.cput.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.Delivery;
import za.ac.cput.repository.IDeliveryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private IDeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Delivery delivery;

    @BeforeEach
    void setUp() {
        delivery = new Delivery();
        delivery.setDeliveryId("1");
    }

    @Test
    void testSaveDelivery() {
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        Delivery saved = deliveryService.save(delivery);

        assertNotNull(saved);
        assertEquals("1", saved.getDeliveryId());
        verify(deliveryRepository, times(1)).save(delivery);
    }

    @Test
    void testFindDeliveryById() {
        when(deliveryRepository.findById("1")).thenReturn(Optional.of(delivery));

        Optional<Delivery> found = deliveryService.findById("1");

        assertTrue(found.isPresent());
        assertEquals("1", found.get().getDeliveryId());
        verify(deliveryRepository, times(1)).findById("1");
    }
}
