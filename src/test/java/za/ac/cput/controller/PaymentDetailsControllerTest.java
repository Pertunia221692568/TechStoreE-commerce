package za.ac.cput.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.service.PaymentDetailsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentDetailsService paymentDetailsService;

    @Test
    void testGetAllPaymentDetails() throws Exception {
        mockMvc.perform(get("/payment-details/getall"))
                .andExpect(status().isOk());
    }
}