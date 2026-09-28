/* OrderControllerTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Order;
import za.ac.cput.domain.Products;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.factory.ProductsFactory;
import za.ac.cput.repository.ICustomerRepository;
import za.ac.cput.repository.IProductsRepository;
import za.ac.cput.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IProductsRepository productRepository;

    private Customer customer;
    private Products product;

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(CustomerFactory.create(
                "Pertunia", "Sifunda",
                "order-ctrl-" + System.currentTimeMillis() + "@test.co.za",
                "071 000 1111", "10 Long Street"));

        product = productRepository.save(ProductsFactory.create(
                "Ctrl Test Laptop " + System.currentTimeMillis(),
                "Dell", "Test", new BigDecimal("12000.00"), 20, "Laptop"));
    }

    @Test
    @DisplayName("GET /api/orders should return all orders")
    void getAll_shouldReturnOrders() throws Exception {
        orderService.placeOrder(customer.getId(), "Address",
                List.of(product.getId()), List.of(1));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/orders?status=PENDING should filter by status")
    void getAll_shouldFilterByStatus() throws Exception {
        orderService.placeOrder(customer.getId(), "Address",
                List.of(product.getId()), List.of(1));

        mockMvc.perform(get("/api/orders").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return order when found")
    void getById_shouldReturnOrder() throws Exception {
        Order order = orderService.placeOrder(customer.getId(), "Address",
                List.of(product.getId()), List.of(1));

        mockMvc.perform(get("/api/orders/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 404 when not found")
    void getById_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/orders/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/orders/customer/{customerId} should return customer orders")
    void getByCustomer_shouldReturnOrders() throws Exception {
        orderService.placeOrder(customer.getId(), "Address",
                List.of(product.getId()), List.of(1));

        mockMvc.perform(get("/api/orders/customer/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/orders should place a new order")
    void placeOrder_shouldCreateOrder() throws Exception {
        String json = """
                {
                  "customerId": %d,
                  "shippingAddress": "10 Long Street, Cape Town",
                  "productIds": [%d],
                  "quantities": [1]
                }
                """.formatted(customer.getId(), product.getId());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.orderItems").isArray());
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/status should update status")
    void updateStatus_shouldUpdate() throws Exception {
        Order order = orderService.placeOrder(customer.getId(), "Address",
                List.of(product.getId()), List.of(1));

        String json = """
                { "status": "CONFIRMED" }
                """;

        mockMvc.perform(patch("/api/orders/" + order.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/cancel should cancel order")
    void cancelOrder_shouldCancel() throws Exception {
        Order order = orderService.placeOrder(customer.getId(), "Address",
                List.of(product.getId()), List.of(1));

        mockMvc.perform(post("/api/orders/" + order.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} should return 204")
    void delete_shouldReturnNoContent() throws Exception {
        Order order = orderService.placeOrder(customer.getId(), "Address",
                List.of(product.getId()), List.of(1));

        mockMvc.perform(delete("/api/orders/" + order.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/orders/stats should return dashboard stats")
    void getStats_shouldReturnStats() throws Exception {
        mockMvc.perform(get("/api/orders/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").exists())
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.pendingOrders").exists());
    }
}

