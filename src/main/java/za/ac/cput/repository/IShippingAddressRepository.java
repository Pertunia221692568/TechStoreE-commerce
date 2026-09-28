package za.ac.cput.repository;

import za.ac.cput.domain.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
}