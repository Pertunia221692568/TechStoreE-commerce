/* ProductFactoryTest.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Products;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class ProductFactoryTest {

    @Test
    @DisplayName("Should create Product with all fields correctly set")
    void shouldCreateProductWithAllFields() {
        Products product = ProductsFactory.create(
                "Dell XPS 15", "Dell",
                "High-performance laptop",
                new BigDecimal("24999.99"), 10, "Laptop");

        assertNotNull(product);
        assertEquals("Dell XPS 15", product.getName());
        assertEquals("Dell", product.getBrand());
        assertEquals(new BigDecimal("24999.99"), product.getPrice());
        assertEquals(10, product.getStockQuantity());
        assertEquals("Laptop", product.getCategory());
    }

    @Test
    @DisplayName("Should create Laptop using createLaptop method")
    void shouldCreateLaptopWithCorrectCategory() {
        Products laptop = ProductsFactory.createLaptop(
                "HP Pavilion", "HP", "Everyday laptop",
                new BigDecimal("12999.00"), 5);

        assertEquals("Laptop", laptop.getCategory());
        assertEquals("HP", laptop.getBrand());
    }

    @Test
    @DisplayName("Should create Accessory using createAccessory method")
    void shouldCreateAccessoryWithCorrectCategory() {
        Products accessory = ProductsFactory.createAccessory(
                "Logitech MX Keys", "Logitech",
                "Wireless keyboard", new BigDecimal("999.00"), 20);

        assertEquals("Accessory", accessory.getCategory());
    }

    @Test
    @DisplayName("Should throw exception when name is blank")
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalStateException.class,
                () -> ProductsFactory.create("", "Dell", "desc",
                        new BigDecimal("100"), 5, "Laptop"));
    }

    @Test
    @DisplayName("Should throw exception when brand is blank")
    void shouldThrowWhenBrandIsBlank() {
        assertThrows(IllegalStateException.class,
                () -> ProductsFactory.create("Laptop", "", "desc",
                        new BigDecimal("100"), 5, "Laptop"));
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void shouldThrowWhenPriceIsNegative() {
        assertThrows(IllegalStateException.class,
                () -> ProductsFactory.create("Laptop", "Dell", "desc",
                        new BigDecimal("-1"), 5, "Laptop"));
    }

    @Test
    @DisplayName("Product should be in stock when stock > 0")
    void productShouldBeInStockWhenStockIsPositive() {
        Products product = ProductsFactory.createLaptop(
                "Lenovo IdeaPad", "Lenovo", "Budget laptop",
                new BigDecimal("9999.00"), 3);

        assertTrue(product.isInStock());
    }

    @Test
    @DisplayName("Product should not be in stock when stock is 0")
    void productShouldNotBeInStockWhenStockIsZero() {
        Products product = ProductsFactory.createLaptop(
                "Lenovo IdeaPad", "Lenovo", "Budget laptop",
                new BigDecimal("9999.00"), 0);

        assertFalse(product.isInStock());
    }
}

