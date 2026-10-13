package za.ac.cput.service;

import za.ac.cput.domain.Delivery;
import za.ac.cput.repository.IDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private IDeliveryRepository repository;

    public Delivery save(Delivery delivery) {
        return repository.save(delivery);
    }

    public Optional<Delivery> findById(String id) {
        return repository.findById(id);
    }

    public List<Delivery> findAll() {
        return repository.findAll();
    }

    public Delivery update(Delivery delivery) {
        return repository.save(delivery);
    }

    public boolean delete(Delivery delivery) {
        repository.delete(delivery);
        return true;
    }
}