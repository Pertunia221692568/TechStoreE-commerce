package za.ac.cput.service;

import za.ac.cput.domain.ShoppingCart;
import za.ac.cput.repository.IShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private IShoppingCartRepository repository;

    public ShoppingCart save(ShoppingCart cart) {
        return repository.save(cart);
    }

    public List<ShoppingCart> findAll() {
        return repository.findAll();
    }

    public ShoppingCart update(ShoppingCart cart) {
        return repository.save(cart);
    }

    public boolean delete(ShoppingCart cart) {
        repository.delete(cart);
        return true;
    }
}