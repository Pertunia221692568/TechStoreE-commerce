package za.ac.cput.service;

import java.util.List;
import java.util.Optional;
import za.ac.cput.domain.ShippingAddress;
import za.ac.cput.repository.IShippingAddressRepository;
import org.springframework.stereotype.Service;

@Service
public class ShippingAddressService {

    private final IShippingAddressRepository repository;

    public ShippingAddressService(IShippingAddressRepository repository) {
        this.repository = repository;
    }

    public ShippingAddress create(ShippingAddress address) {
        return repository.save(address);
    }

    public Optional<ShippingAddress> get(Long id) {
        return repository.findById(id);
    }

    public List<ShippingAddress> getAll() {
        return repository.findAll();
    }

    public ShippingAddress update(ShippingAddress address) {
        return repository.save(address);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
