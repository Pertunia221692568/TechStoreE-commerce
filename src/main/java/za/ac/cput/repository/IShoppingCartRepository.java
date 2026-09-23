package za.ac.cput.repository;

import za.ac.cput.domain.ShoppingCart;
import java.util.List;

public interface IShoppingCartRepository {
    ShoppingCart save(ShoppingCart cart);
    List<ShoppingCart> findAll();
    ShoppingCart update(ShoppingCart cart);
    boolean delete(ShoppingCart cart);
}
