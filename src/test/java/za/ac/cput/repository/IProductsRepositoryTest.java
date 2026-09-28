/* IProductsRepositoryTest.java
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Products;
import za.ac.cput.factory.ProductsFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IProductsRepositoryTest {

    @Autowired
    private IProductsRepository productRepository;

    @Test
    @DisplayName("Should save and find a product by ID")
    void shouldSaveAndFindById() {
        Products product = ProductsFactory.createLaptop(
                "Test Dell XPS " + System.currentTimeMillis(), "Dell", "Premium laptop",
                new BigDecimal("24999.99"), 5);
        Products saved = productRepository.save(product);

        Optional<Products> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertTrue(found.get().getName().contains("Dell XPS"));
    }

    @Test
    @DisplayName("Should find products by category")
    void shouldFindByCategory() {
        String unique = String.valueOf(System.currentTimeMillis());
        productRepository.save(ProductsFactory.createLaptop("HP-" + unique, "HP", "desc", new BigDecimal("15000"), 3));
        productRepository.save(ProductsFactory.createAccessory("Mouse-" + unique, "Logitech", "desc", new BigDecimal("500"), 10));

        List<Products> laptops = productRepository.findByCategory("Laptop");
        assertFalse(laptops.isEmpty());
        assertTrue(laptops.stream().anyMatch(p -> p.getName().contains("HP-" + unique)));
    }

    @Test
    @DisplayName("Should find products by name containing keyword")
    void shouldFindByNameContaining() {
        String unique = "UniqueBrand" + System.currentTimeMillis();
        productRepository.save(ProductsFactory.createLaptop(unique + " XPS", "Dell", "desc", new BigDecimal("24999"), 5));

        List<Products> found = productRepository.findByNameContainingIgnoreCase(unique.toLowerCase());
        assertFalse(found.isEmpty());
        assertTrue(found.get(0).getName().contains(unique));
    }

    @Test
    @DisplayName("Should find products with stock greater than 0")
    void shouldFindProductsInStock() {
        String unique = "InStock" + System.currentTimeMillis();
        productRepository.save(ProductsFactory.createLaptop(unique, "Dell", "desc", new BigDecimal("10000"), 5));

        List<Products> inStock = productRepository.findByStockQuantityGreaterThan(0);
        assertFalse(inStock.isEmpty());
        assertTrue(inStock.stream().anyMatch(p -> p.getName().equals(unique)));
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void shouldDeleteById() {
        Products product = productRepository.save(
                ProductsFactory.createLaptop("ToDelete-" + System.currentTimeMillis(), "Test", "desc", new BigDecimal("5000"), 1));

        Long id = product.getId();
        productRepository.deleteById(id);

        assertFalse(productRepository.findById(id).isPresent());
    }
}