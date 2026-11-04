package za.ac.cput.repository;

import za.ac.cput.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByShippingId(String shippingId);
}