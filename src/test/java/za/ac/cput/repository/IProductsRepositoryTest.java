/* IProductRepositoryTest.java
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Products;
import za.ac.cput.factory.ProductsFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class IProductRepositoryTest {

    @Autowired
    private IProductsRepository productRepository;

    @Test
    @DisplayName("Should save and find a product by ID")
    void shouldSaveAndFindById() {
        Products product = ProductsFactory.createLaptop(
                "Dell XPS 15", "Dell", "Premium laptop",
                new BigDecimal("24999.99"), 5);
        Products saved = productRepository.save(product);

        Optional<Products> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Dell XPS 15", found.get().getName());
    }

    @Test
    @DisplayName("Should find products by category")
    void shouldFindByCategory() {
        productRepository.save(ProductsFactory.createLaptop("HP", "HP", "desc", new BigDecimal("15000"), 3));
        productRepository.save(ProductsFactory.createLaptop("Lenovo", "Lenovo", "desc", new BigDecimal("12000"), 2));
        productRepository.save(ProductsFactory.createAccessory("Mouse", "Logitech", "desc", new BigDecimal("500"), 10));

        List<Products> laptops = productRepository.findByCategory("Laptop");
        assertEquals(2, laptops.size());
    }

    @Test
    @DisplayName("Should find products by name containing keyword")
    void shouldFindByNameContaining() {
        productRepository.save(ProductsFactory.createLaptop("Dell XPS 15", "Dell", "desc", new BigDecimal("24999"), 5));
        productRepository.save(ProductsFactory.createLaptop("Dell Inspiron", "Dell", "desc", new BigDecimal("12999"), 8));

        List<Products> dellProducts = productRepository.findByNameContainingIgnoreCase("dell");
        assertEquals(2, dellProducts.size());
    }

    @Test
    @DisplayName("Should find products with stock greater than 0")
    void shouldFindProductsInStock() {
        productRepository.save(ProductsFactory.createLaptop("In Stock", "Dell", "desc", new BigDecimal("10000"), 5));
        productRepository.save(ProductsFactory.createLaptop("Out of Stock", "HP", "desc", new BigDecimal("10000"), 0));

        List<Products> inStock = productRepository.findByStockQuantityGreaterThan(0);
        assertEquals(1, inStock.size());
        assertEquals("In Stock", inStock.get(0).getName());
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void shouldDeleteById() {
        Products product = productRepository.save(
                ProductsFactory.createLaptop("Test", "Test", "desc", new BigDecimal("5000"), 1));
        productRepository.deleteById(product.getId());

        assertFalse(productRepository.findById(product.getId()).isPresent());
    }
}

