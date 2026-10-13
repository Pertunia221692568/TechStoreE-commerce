import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';

function formatPrice(price) {
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(price));
}

export default function Cart() {
  const { items, removeFromCart, updateQuantity, subtotal, clearCart } = useCart();
  const navigate = useNavigate();

  if (items.length === 0) {
    return (
      <div className="mx-auto max-w-7xl px-4 py-20 text-center animate-fade-in">
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-slate-100">
          <svg className="h-10 w-10 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        </div>
        <h2 className="font-display text-2xl font-bold text-slate-900">Your cart is empty</h2>
        <p className="mt-2 text-slate-500">Looks like you haven't added anything yet.</p>
        <Link to="/products" className="btn-primary mt-6 inline-flex">Start shopping</Link>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
      <div className="mb-8 flex items-center justify-between">
        <h1 className="font-display text-3xl font-bold text-slate-900">Shopping cart</h1>
        <button onClick={clearCart} className="text-sm font-medium text-red-600 hover:text-red-700">
          Clear cart
        </button>
      </div>

      <div className="grid gap-8 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-4">
          {items.map(({ product, quantity }) => (
            <div key={product.id} className="card flex gap-4 p-4">
              <div className="flex h-24 w-24 shrink-0 items-center justify-center rounded-xl bg-brand-50">
                <svg className="h-10 w-10 text-brand-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
              </div>
              <div className="flex flex-1 flex-col">
                <div className="flex justify-between gap-2">
                  <div>
                    <Link to={`/products/${product.id}`} className="font-semibold text-slate-900 hover:text-brand-600">
                      {product.name}
                    </Link>
                    <p className="text-xs text-slate-400">{product.brand} · {product.category}</p>
                  </div>
                  <p className="font-semibold text-slate-900">{formatPrice(product.price)}</p>
                </div>
                <div className="mt-auto flex items-center justify-between pt-3">
                  <div className="flex items-center rounded-lg border border-slate-200">
                    <button onClick={() => updateQuantity(product.id, quantity - 1)} className="px-2.5 py-1 text-slate-600 hover:text-brand-600">−</button>
                    <span className="w-8 text-center text-sm font-medium">{quantity}</span>
                    <button onClick={() => updateQuantity(product.id, quantity + 1)} className="px-2.5 py-1 text-slate-600 hover:text-brand-600">+</button>
                  </div>
                  <button onClick={() => removeFromCart(product.id)} className="text-xs font-medium text-red-500 hover:text-red-700">
                    Remove
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="card h-fit p-6">
          <h2 className="font-display text-lg font-bold text-slate-900">Order summary</h2>
          <div className="mt-4 space-y-3 text-sm">
            <div className="flex justify-between">
              <span className="text-slate-500">Subtotal</span>
              <span className="font-medium">{formatPrice(subtotal)}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Shipping</span>
              <span className="font-medium text-emerald-600">Calculated at checkout</span>
            </div>
            <div className="border-t border-slate-100 pt-3 flex justify-between text-base">
              <span className="font-semibold">Total</span>
              <span className="font-display font-bold text-slate-900">{formatPrice(subtotal)}</span>
            </div>
          </div>
          <button onClick={() => navigate('/checkout')} className="btn-primary mt-6 w-full !py-3">
            Proceed to checkout
          </button>
          <Link to="/products" className="mt-3 block text-center text-sm font-medium text-brand-600 hover:text-brand-700">
            Continue shopping
          </Link>
        </div>
      </div>
    </div>
  );
}
