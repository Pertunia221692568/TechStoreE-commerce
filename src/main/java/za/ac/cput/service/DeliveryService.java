package za.ac.cput.service;

import za.ac.cput.domain.Delivery;
import za.ac.cput.factory.DeliveryFactory;
import za.ac.cput.repository.IDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private IDeliveryRepository repository;

    public Delivery save(Delivery delivery) {
        return repository.save(delivery);
    }

    public Delivery createDelivery(
            String trackingNumber,
            String carrier,
            LocalDateTime estimatedDeliveryDate,
            int productUnits,
            Long orderId
    ) {
        Delivery delivery = DeliveryFactory.createDelivery(
                trackingNumber, carrier, estimatedDeliveryDate, productUnits, orderId
        );
        if (delivery == null) {
            throw new IllegalArgumentException("Invalid delivery details");
        }
        return repository.save(delivery);
    }

    public Optional<Delivery> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Delivery> findByShippingId(String shippingId) {
        return repository.findByShippingId(shippingId);
    }

    public List<Delivery> findAll() {
        return repository.findAll();
    }

    public Delivery update(Delivery delivery) {
        return repository.save(delivery);
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}