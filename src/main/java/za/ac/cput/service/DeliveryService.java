package za.ac.cput.service;

import za.ac.cput.domain.Delivery;
import za.ac.cput.factory.DeliveryFactory;
import za.ac.cput.repository.IDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    /** Maximum product units that can be confirmed for dispatch on a single day */
    public static final int DAILY_CAPACITY = 50;

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

    public int getConfirmedUnitsForDay(LocalDate day) {
        return repository.sumConfirmedUnitsForDay(day);
    }

    public int getRemainingCapacity(LocalDate day) {
        return Math.max(0, DAILY_CAPACITY - getConfirmedUnitsForDay(day));
    }

    public CapacityInfo getTodayCapacity() {
        LocalDate today = LocalDate.now();
        int used = getConfirmedUnitsForDay(today);
        return new CapacityInfo(DAILY_CAPACITY, used, Math.max(0, DAILY_CAPACITY - used));
    }

    public record CapacityInfo(int dailyLimit, int used, int remaining) {}
}