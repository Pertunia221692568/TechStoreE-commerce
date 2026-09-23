/* IProductRepository.java class
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Extends JpaRepository which provides save, findById, findAll, deleteById.
 */
@Repository
public interface IProductsRepository extends JpaRepository<Products, Long> {

    List<Products> findByCategory(String category);

    List<Products> findByBrand(String brand);

    List<Products> findByNameContainingIgnoreCase(String name);

    List<Products> findByStockQuantityGreaterThan(int quantity);

    List<Products> findByPriceBetween(BigDecimal min, BigDecimal max);
}
