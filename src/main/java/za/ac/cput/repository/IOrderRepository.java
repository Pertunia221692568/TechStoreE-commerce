
/* IOrderRepository.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);


    List<Order> findByStatus(Order.Status status);


    List<Order> findByCustomerIdAndStatus(Long customerId, Order.Status status);


    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal calculateTotalRevenue();
}
