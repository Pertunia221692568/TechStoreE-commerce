/* Service Layer
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.service;

import za.ac.cput.domain.Products;
import za.ac.cput.factory.ProductsFactory;
import za.ac.cput.repository.IProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductsService {

    private final IProductsRepository productRepository;

    public ProductsService(IProductsRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Products> findAll() {
        return productRepository.findAll();
    }

    public Optional<Products> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Products> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Products> search(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Products> findInStock() {
        return productRepository.findByStockQuantityGreaterThan(0);
    }

    public Products create(String name, String brand, String description,
                          BigDecimal price, int stock, String category) {
        Products product = ProductsFactory.create(name, brand, description, price, stock, category);
        return productRepository.save(product);
    }

    public Products update(Long id, String name, String brand, String description,
                          BigDecimal price, int stock, String category) {
        Products product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        product.setName(name);
        product.setBrand(brand);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stock);
        product.setCategory(category);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}

