/* CustomerControllerTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import za.ac.cput.domain.Customer;
import za.ac.cput.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    private String uniqueEmail;

    @BeforeEach
    void setUp() {
        uniqueEmail = "ctrl-test-" + System.currentTimeMillis() + "@test.co.za";
    }

    @Test
    @DisplayName("GET /api/customers should return list of customers")
    void getAll_shouldReturnCustomers() throws Exception {
        customerService.register("Test", "User", uniqueEmail, "", "");

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/customers/{id} should return customer when found")
    void getById_shouldReturnCustomer() throws Exception {
        Customer saved = customerService.register(
                "Pertunia", "Sifunda", uniqueEmail,
                "071 000 1111", "10 Long Street");

        mockMvc.perform(get("/api/customers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Pertunia"))
                .andExpect(jsonPath("$.email").value(uniqueEmail));
    }

    @Test
    @DisplayName("GET /api/customers/{id} should return 404 when not found")
    void getById_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/customers/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/customers should register a new customer")
    void register_shouldCreateCustomer() throws Exception {
        String json = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "email": "%s",
                  "phone": "082 111 2222",
                  "address": "Cape Town"
                }
                """.formatted(uniqueEmail);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value(uniqueEmail));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} should update customer")
    void update_shouldUpdateCustomer() throws Exception {
        Customer saved = customerService.register(
                "Old", "Name", uniqueEmail, "", "Old Address");

        String json = """
                {
                  "firstName": "Updated",
                  "lastName": "Name",
                  "email": "updated-%d@test.com",
                  "phone": "083 000 0000",
                  "address": "New Address"
                }
                """.formatted(System.currentTimeMillis());

        mockMvc.perform(put("/api/customers/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.address").value("New Address"));
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} should return 204")
    void delete_shouldReturnNoContent() throws Exception {
        Customer saved = customerService.register(
                "ToDelete", "User", uniqueEmail, "", "");

        mockMvc.perform(delete("/api/customers/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
