package za.ac.cput.factory;

import za.ac.cput.domain.ShoppingCart;
import za.ac.cput.util.Helper;
import java.time.LocalDateTime;

public class ShoppingCartFactory {
    public static ShoppingCart createShoppingCart(Integer totalItems) {
        if (totalItems == null || totalItems < 0) {
            return null;
        }
        String cartId = Helper.generateId();
        LocalDateTime now = LocalDateTime.now();
        return new ShoppingCart(cartId, now, now, totalItems);
    }
}
