import { createContext, useContext, useEffect, useReducer } from 'react';

const CartContext = createContext(null);

const initialState = {
  items: JSON.parse(localStorage.getItem('techvault_cart') || '[]'),
};

function cartReducer(state, action) {
  switch (action.type) {
    case 'ADD': {
      const existing = state.items.find(i => i.product.id === action.product.id);
      let items;
      if (existing) {
        items = state.items.map(i =>
          i.product.id === action.product.id
            ? { ...i, quantity: Math.min(i.quantity + (action.quantity || 1), i.product.stockQuantity) }
            : i
        );
      } else {
        items = [...state.items, { product: action.product, quantity: action.quantity || 1 }];
      }
      return { ...state, items };
    }
    case 'REMOVE':
      return { ...state, items: state.items.filter(i => i.product.id !== action.productId) };
    case 'UPDATE_QTY':
      return {
        ...state,
        items: state.items.map(i =>
          i.product.id === action.productId
            ? { ...i, quantity: Math.max(1, Math.min(action.quantity, i.product.stockQuantity)) }
            : i
        ),
      };
    case 'CLEAR':
      return { ...state, items: [] };
    default:
      return state;
  }
}

export function CartProvider({ children }) {
  const [state, dispatch] = useReducer(cartReducer, initialState);

  useEffect(() => {
    localStorage.setItem('nexora_cart', JSON.stringify(state.items));
  }, [state.items]);

  const addToCart = (product, quantity = 1) => dispatch({ type: 'ADD', product, quantity });
  const removeFromCart = (productId) => dispatch({ type: 'REMOVE', productId });
  const updateQuantity = (productId, quantity) => dispatch({ type: 'UPDATE_QTY', productId, quantity });
  const clearCart = () => dispatch({ type: 'CLEAR' });

  const totalItems = state.items.reduce((sum, i) => sum + i.quantity, 0);
  const subtotal = state.items.reduce(
    (sum, i) => sum + Number(i.product.price) * i.quantity,
    0
  );

  return (
    <CartContext.Provider
      value={{
        items: state.items,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        totalItems,
        subtotal,
      }}
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
