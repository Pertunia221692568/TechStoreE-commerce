package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.ShoppingCart;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartFactoryTest {

    @Test
    void testCreateShoppingCart() {
        ShoppingCart cart = ShoppingCartFactory.createShoppingCart(3);
        assertNotNull(cart);
        assertNotNull(cart.getCartId());
        assertEquals(3, cart.getTotalItems());
    }
}
