package za.ac.cput.service;

import java.util.List;
import za.ac.cput.domain.ShippingAddress;
import za.ac.cput.repository.IShippingAddressRepository;

public class ShippingAddressService {

    private final IShippingAddressRepository repository;

    public ShippingAddressService(IShippingAddressRepository repository) {
        this.repository = repository;
    }

    public ShippingAddress create(ShippingAddress address) {
        return repository.save(address);
    }

    public List<ShippingAddress> getAll() {
        return repository.findAll();
    }

    public ShippingAddress update(ShippingAddress address) {
        return repository.update(address);
    }

    public boolean delete(ShippingAddress address) {
        return repository.delete(address);
    }
}
