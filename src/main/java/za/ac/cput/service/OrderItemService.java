package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.OrderItem;
import za.ac.cput.repository.IOrderItemRepository;

import java.util.List;

@Service
public class OrderItemService {

    private final IOrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(IOrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItem> getAll() {
        return this.orderItemRepository.findAll();
    }

    public List<OrderItem> getByOrderId(Long orderId) {
        return this.orderItemRepository.findByOrderId(orderId);
    }

    public List<OrderItem> getByProductId(Long productId) {
        return this.orderItemRepository.findByProductId(productId);
    }
}
