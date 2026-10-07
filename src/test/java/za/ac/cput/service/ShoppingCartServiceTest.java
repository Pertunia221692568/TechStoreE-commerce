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
        cart.setId(1L);
    }

    @Test
    void testSaveShoppingCart() {
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

        ShoppingCart saved = shoppingCartService.save(cart);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(shoppingCartRepository, times(1)).save(cart);
    }

    @Test
    void testFindShoppingCartById() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));

        Optional<ShoppingCart> found = shoppingCartService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(shoppingCartRepository, times(1)).findById(1L);
    }
}
