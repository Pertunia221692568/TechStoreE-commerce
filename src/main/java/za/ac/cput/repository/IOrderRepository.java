
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

    /** Get all orders for a specific customer, newest first. */
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);

    /** Get orders by status (PENDING, CONFIRMED, SHIPPED) */
    List<Order> findByStatus(Order.Status status);

    /** Get orders for a customer with a specific status. */
    List<Order> findByCustomerIdAndStatus(Long customerId, Order.Status status);

    /** Calculate total revenue from all DELIVERED orders. */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal calculateTotalRevenue();
}
