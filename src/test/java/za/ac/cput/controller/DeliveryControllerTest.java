package za.ac.cput.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.service.DeliveryService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @Test
    void testGetDelivery() throws Exception {
        mockMvc.perform(get("/delivery/get/1"))
                .andExpect(status().isOk());
    }
}
