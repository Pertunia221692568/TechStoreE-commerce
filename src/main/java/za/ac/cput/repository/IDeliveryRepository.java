package za.ac.cput.repository;

import za.ac.cput.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IDeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByShippingId(String shippingId);

    /**
     * Sum of productUnits for deliveries whose estimated date falls on the given day
     * and status is CONFIRMED (or later). Used for daily shipping capacity.
     */
    @Query("""
        SELECT COALESCE(SUM(d.productUnits), 0)
        FROM Delivery d
        WHERE FUNCTION('DATE', d.estimatedDeliveryDate) = :day
          AND d.status IN ('CONFIRMED', 'DISPATCHED', 'IN_TRANSIT', 'DELIVERED')
        """)
    int sumConfirmedUnitsForDay(@Param("day") LocalDate day);
}