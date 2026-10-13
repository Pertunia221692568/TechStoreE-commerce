package za.ac.cput.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.ShoppingCart;
import za.ac.cput.repository.IShoppingCartRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private IShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        cart.setCartId("1");
    }

    @Test
    void testSaveShoppingCart() {
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

        ShoppingCart saved = shoppingCartService.save(cart);

        assertNotNull(saved);
        assertEquals("1", saved.getCartId());
        verify(shoppingCartRepository, times(1)).save(cart);
    }

    @Test
    void testFindShoppingCartById() {
        when(shoppingCartRepository.findById("1")).thenReturn(Optional.of(cart));

        Optional<ShoppingCart> found = shoppingCartService.findById("1");

        assertTrue(found.isPresent());
        assertEquals("1", found.get().getCartId());
        verify(shoppingCartRepository, times(1)).findById("1");
    }
}