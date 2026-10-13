import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { placeOrder } from '../services/api';

function formatPrice(price) {
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(price));
}

export default function Checkout() {
  const { items, subtotal, clearCart } = useCart();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [shippingAddress, setShippingAddress] = useState(user?.address || '');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(null);

  if (items.length === 0 && !success) {
    return (
      <div className="mx-auto max-w-7xl px-4 py-20 text-center">
        <p className="text-slate-500">Your cart is empty.</p>
        <Link to="/products" className="btn-primary mt-4 inline-flex">Shop now</Link>
      </div>
    );
  }

  if (success) {
    return (
      <div className="mx-auto max-w-lg px-4 py-20 text-center animate-scale-in">
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-emerald-100">
          <svg className="h-10 w-10 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <h2 className="font-display text-2xl font-bold text-slate-900">Order placed!</h2>
        <p className="mt-2 text-slate-500">
          Order #{success.id} has been created successfully.
        </p>
        <p className="mt-1 text-sm text-slate-400">Total: {formatPrice(success.totalAmount)}</p>
        <div className="mt-8 flex justify-center gap-3">
          <Link to="/orders" className="btn-primary">View orders</Link>
          <Link to="/products" className="btn-secondary">Continue shopping</Link>
        </div>
      </div>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      setError('Please sign in to place an order.');
      return;
    }
    if (!shippingAddress.trim()) {
      setError('Shipping address is required.');
      return;
    }

    setLoading(true);
    setError('');
    try {
      const productIds = items.map((i) => i.product.id);
      const quantities = items.map((i) => i.quantity);
      const order = await placeOrder({
        customerId: user.id,
        shippingAddress: shippingAddress.trim(),
        productIds,
        quantities,
      });
      clearCart();
      setSuccess(order);
    } catch (err) {
      console.error(err);
      const msg = err.response?.data?.error || err.response?.data?.message || err.message || 'Failed to place order';
      setError(typeof msg === 'string' ? msg : 'Failed to place order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
      <h1 className="mb-8 font-display text-3xl font-bold text-slate-900">Checkout</h1>

      {!isAuthenticated && (
        <div className="mb-6 rounded-xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">
          You need to <Link to="/login" className="font-semibold underline">sign in</Link> before placing an order.
        </div>
      )}

      <form onSubmit={handleSubmit} className="grid gap-8 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-6">
          <div className="card p-6">
            <h2 className="font-display text-lg font-bold text-slate-900">Shipping address</h2>
            <textarea
              value={shippingAddress}
              onChange={(e) => setShippingAddress(e.target.value)}
              rows={3}
              placeholder="Street, suburb, city, postal code"
              className="input-field mt-4"
              required
            />
          </div>

          <div className="card p-6">
            <h2 className="font-display text-lg font-bold text-slate-900">Items</h2>
            <ul className="mt-4 divide-y divide-slate-100">
              {items.map(({ product, quantity }) => (
                <li key={product.id} className="flex justify-between py-3 text-sm">
                  <span className="text-slate-700">
                    {product.name} <span className="text-slate-400">× {quantity}</span>
                  </span>
                  <span className="font-medium">{formatPrice(Number(product.price) * quantity)}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <div className="card h-fit p-6">
          <h2 className="font-display text-lg font-bold text-slate-900">Summary</h2>
          <div className="mt-4 space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-slate-500">Subtotal</span>
              <span className="font-medium">{formatPrice(subtotal)}</span>
            </div>
            <div className="border-t border-slate-100 pt-3 flex justify-between text-base">
              <span className="font-semibold">Total</span>
              <span className="font-display font-bold">{formatPrice(subtotal)}</span>
            </div>
          </div>

          {error && (
            <p className="mt-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>
          )}

          <button
            type="submit"
            disabled={loading || !isAuthenticated}
            className="btn-primary mt-6 w-full !py-3"
          >
            {loading ? 'Placing order...' : 'Place order'}
          </button>
        </div>
      </form>
    </div>
  );
}
