/* OrderItem Repository Interface
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);
}

