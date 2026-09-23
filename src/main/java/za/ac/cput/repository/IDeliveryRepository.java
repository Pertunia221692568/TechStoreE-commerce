package za.ac.cput.repository;

import za.ac.cput.domain.Delivery;
import java.util.List;

public interface IDeliveryRepository {
    Delivery save(Delivery delivery);
    List<Delivery> findAll();
    Delivery update(Delivery delivery);
    boolean delete(Delivery delivery);
}
