package za.ac.cput.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.repository.IPaymentDetailsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDetailsServiceTest {

    @Mock
    private IPaymentDetailsRepository paymentDetailsRepository;

    @InjectMocks
    private PaymentDetailsService paymentDetailsService;

    private PaymentDetails paymentDetails;

    @BeforeEach
    void setUp() {
        paymentDetails = new PaymentDetails();
        paymentDetails.setId(1L);
    }

    @Test
    void testSavePaymentDetails() {
        when(paymentDetailsRepository.save(any(PaymentDetails.class))).thenReturn(paymentDetails);

        PaymentDetails saved = paymentDetailsService.save(paymentDetails);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(paymentDetailsRepository, times(1)).save(paymentDetails);
    }

    @Test
    void testFindPaymentDetailsById() {
        when(paymentDetailsRepository.findById(1L)).thenReturn(Optional.of(paymentDetails));

        Optional<PaymentDetails> found = paymentDetailsService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(paymentDetailsRepository, times(1)).findById(1L);
    }
}
