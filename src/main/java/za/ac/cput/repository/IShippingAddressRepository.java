package za.ac.cput.repository;


import za.ac.cput.domain.ShippingAddress;
import java.util.List;

public interface IShippingAddressRepository {

    ShippingAddress save(ShippingAddress address);

    List<ShippingAddress> findAll();

    ShippingAddress update(ShippingAddress address);

    boolean delete(ShippingAddress address);
}
