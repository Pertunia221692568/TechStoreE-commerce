import { createContext, useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';
import {
    getCart,
    addCartItem,
    updateCartItem,
    removeCartItem,
    clearCartItems,
} from '../services/api';

const CartContext = createContext(null);

// Every signed-in user gets their own random cart id (remembered in this browser).
// A cart therefore always belongs to one user: signing out hides it, and a different
// user on the same computer never sees it. The cart itself is stored in the database.
function getCartId(userId) {
    const key = `techvault_cart_id_${userId}`;
    let id = localStorage.getItem(key);
    if (!id) {
        id = crypto.randomUUID
            ? crypto.randomUUID()
            : `${Date.now()}-${Math.random().toString(36).slice(2)}`;
        localStorage.setItem(key, id);
    }
    return id;
}

// Backend returns [{ id, quantity, product }] -> page code uses [{ product, quantity }]
const toItems = (rows) =>
    (Array.isArray(rows) ? rows : []).map((r) => ({ product: r.product, quantity: r.quantity }));

export function CartProvider({ children }) {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [items, setItems] = useState([]);
    const cartId = user?.id ? getCartId(user.id) : null;

    // Load the signed-in user's cart; show an empty cart when nobody is signed in
    useEffect(() => {
        if (!cartId) {
            setItems([]);
            return;
        }
        getCart(cartId)
            .then((rows) => setItems(toItems(rows)))
            .catch((err) => console.error('Could not load cart', err));
    }, [cartId]);

    // Resolves to true when the server accepted the change, false when it did not
    const run = (request) =>
        request
            .then((rows) => {
                setItems(toItems(rows));
                return true;
            })
            .catch((err) => {
                console.error(err);
                alert(err.response?.data?.error || 'Could not update the cart');
                return false;
            });

    // Carts belong to accounts, so a visitor who is not signed in is sent to sign in
    const addToCart = (product, quantity = 1) => {
        if (!cartId) {
            navigate('/login');
            return Promise.resolve(false);
        }
        return run(addCartItem(cartId, product.id, quantity));
    };
    const removeFromCart = (productId) =>
        cartId ? run(removeCartItem(cartId, productId)) : Promise.resolve(false);
    const updateQuantity = (productId, quantity) =>
        cartId ? run(updateCartItem(cartId, productId, quantity)) : Promise.resolve(false);
    const clearCart = () => (cartId ? run(clearCartItems(cartId)) : Promise.resolve(false));

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
