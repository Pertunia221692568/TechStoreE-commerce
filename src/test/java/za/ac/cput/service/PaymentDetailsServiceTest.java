package za.ac.cput.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.PaymentDetails;
import za.ac.cput.repository.IPaymentDetailsRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDetailsServiceTest {

    @Mock
    private IPaymentDetailsRepository repository;

    @InjectMocks
    private PaymentDetailsService paymentDetailsService;

    private PaymentDetails paymentDetails;

    @BeforeEach
    void setUp() {
        paymentDetails = new PaymentDetails();
        paymentDetails.setPaymentId("PAY-001");
        paymentDetails.setPaymentMethod("CARD");
        paymentDetails.setCardLast4("4242");
        paymentDetails.setCardHolderName("Jane Doe");
        paymentDetails.setCardExpiry("12/28");
        paymentDetails.setAmount(new BigDecimal("999.00"));
        paymentDetails.setStatus("COMPLETED");
    }

    @Test
    void testSavePaymentDetails() {
        when(repository.save(any(PaymentDetails.class))).thenReturn(paymentDetails);

        PaymentDetails saved = paymentDetailsService.save(paymentDetails);

        assertNotNull(saved);
        assertEquals("PAY-001", saved.getPaymentId());
        verify(repository, times(1)).save(paymentDetails);
    }

    @Test
    void testFindPaymentDetailsById() {
        when(repository.findById(1L)).thenReturn(Optional.of(paymentDetails));

        Optional<PaymentDetails> found = paymentDetailsService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("PAY-001", found.get().getPaymentId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindByPaymentId() {
        when(repository.findByPaymentId("PAY-001")).thenReturn(Optional.of(paymentDetails));

        Optional<PaymentDetails> found = paymentDetailsService.findByPaymentId("PAY-001");

        assertTrue(found.isPresent());
        assertEquals("4242", found.get().getCardLast4());
        verify(repository, times(1)).findByPaymentId("PAY-001");
    }
}