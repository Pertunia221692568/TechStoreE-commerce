package za.ac.cput.service;

import za.ac.cput.domain.Delivery;
import za.ac.cput.repository.IDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeliveryService {

    @Autowired
    private IDeliveryRepository repository;

    public Delivery save(Delivery delivery) {
        return repository.save(delivery);
    }

    public List<Delivery> findAll() {
        return repository.findAll();
    }

    public Delivery update(Delivery delivery) {
        return repository.save(delivery);   // use save() for update
    }

    public boolean delete(Delivery delivery) {
        repository.delete(delivery);        // delete() returns void
        return true;
    }
}
