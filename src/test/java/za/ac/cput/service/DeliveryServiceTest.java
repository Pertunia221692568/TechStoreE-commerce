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
        delivery.setId(1L);
    }

    @Test
    void testSaveDelivery() {
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        Delivery saved = deliveryService.save(delivery);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(deliveryRepository, times(1)).save(delivery);
    }

    @Test
    void testFindDeliveryById() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        Optional<Delivery> found = deliveryService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(deliveryRepository, times(1)).findById(1L);
    }
}
