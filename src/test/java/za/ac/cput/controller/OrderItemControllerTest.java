/* OrderItemControllerTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Order;
import za.ac.cput.domain.Products;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.factory.ProductsFactory;
import za.ac.cput.factory.OrderFactory;
import za.ac.cput.repository.ICustomerRepository;
import za.ac.cput.repository.IProductsRepository;
import za.ac.cput.repository.IOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IProductsRepository productRepository;

    private Order order;
    private Products product;

    @BeforeEach
    void setUp() {
        Customer customer = customerRepository.save(CustomerFactory.create(
                "Pertunia", "Sifunda",
                "item-ctrl-" + System.currentTimeMillis() + "@test.co.za",
                "071 000 1111", "10 Long Street"));

        product = productRepository.save(ProductsFactory.create(
                "Item Test Product " + System.currentTimeMillis(),
                "Dell", "Test", new BigDecimal("9000.00"), 15, "Laptop"));

        Order newOrder = OrderFactory.create(customer, "10 Long Street");
        order = orderRepository.save(newOrder);
    }

    @Test
    @DisplayName("GET /api/order-items should return all items")
    void getAll_shouldReturnItems() throws Exception {
        mockMvc.perform(get("/api/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/order-items/order/{orderId} should return items for order")
    void getByOrder_shouldReturnItems() throws Exception {
        mockMvc.perform(get("/api/order-items/order/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/order-items/product/{productId} should return items for product")
    void getByProduct_shouldReturnItems() throws Exception {
        mockMvc.perform(get("/api/order-items/product/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/order-items/{id} should return 404 when not found")
    void getById_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/order-items/999999"))
                .andExpect(status().isNotFound());
    }
}