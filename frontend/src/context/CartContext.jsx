import { createContext, useContext, useEffect, useState } from 'react';
import {
    getCart,
    addCartItem,
    updateCartItem,
    removeCartItem,
    clearCartItems,
} from '../services/api';

const CartContext = createContext(null);

// Each browser gets a random cart id. The cart itself is stored in the database.
function getCartId() {
    let id = localStorage.getItem('techvault_cart_id');
    if (!id) {
        id = crypto.randomUUID
            ? crypto.randomUUID()
            : `${Date.now()}-${Math.random().toString(36).slice(2)}`;
        localStorage.setItem('techvault_cart_id', id);
    }
    return id;
}

// Backend returns [{ id, quantity, product }] -> page code uses [{ product, quantity }]
const toItems = (rows) =>
    (Array.isArray(rows) ? rows : []).map((r) => ({ product: r.product, quantity: r.quantity }));

export function CartProvider({ children }) {
    const [cartId] = useState(getCartId);
    const [items, setItems] = useState([]);

    // Load the cart from the database when the site opens
    useEffect(() => {
        getCart(cartId)
            .then((rows) => setItems(toItems(rows)))
            .catch((err) => console.error('Could not load cart', err));
    }, [cartId]);

    const run = (request) =>
        request
            .then((rows) => setItems(toItems(rows)))
            .catch((err) => {
                console.error(err);
                alert(err.response?.data?.error || 'Could not update the cart');
            });

    const addToCart = (product, quantity = 1) => run(addCartItem(cartId, product.id, quantity));
    const removeFromCart = (productId) => run(removeCartItem(cartId, productId));
    const updateQuantity = (productId, quantity) => run(updateCartItem(cartId, productId, quantity));
    const clearCart = () => run(clearCartItems(cartId));

    const totalItems = items.reduce((sum, i) => sum + i.quantity, 0);
    const subtotal = items.reduce((sum, i) => sum + Number(i.product.price) * i.quantity, 0);

    return (
        <CartContext.Provider
            value={{ items, addToCart, removeFromCart, updateQuantity, clearCart, totalItems, subtotal }}
        >
            {children}
        </CartContext.Provider>
    );
}

export function useCart() {
    const ctx = useContext(CartContext);
    if (!ctx) throw new Error('useCart must be used within CartProvider');
    return ctx;
}
