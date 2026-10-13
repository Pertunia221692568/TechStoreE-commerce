import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getProduct } from '../services/api';
import { useCart } from '../context/CartContext';
import LoadingSpinner from '../components/LoadingSpinner';

function formatPrice(price) {
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(price));
}

export default function ProductDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [qty, setQty] = useState(1);
  const [added, setAdded] = useState(false);
  const { addToCart } = useCart();

  useEffect(() => {
    setLoading(true);
    getProduct(id)
      .then(setProduct)
      .catch(() => setProduct(null))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <LoadingSpinner />;
  if (!product) {
    return (
      <div className="mx-auto max-w-7xl px-4 py-20 text-center">
        <p className="text-slate-500">Product not found.</p>
        <Link to="/products" className="btn-primary mt-4 inline-flex">Back to shop</Link>
      </div>
    );
  }

  const inStock = product.stockQuantity > 0;

  const handleAdd = () => {
    if (!inStock) return;
    addToCart(product, qty);
    setAdded(true);
    setTimeout(() => setAdded(false), 2000);
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
      <nav className="mb-6 text-sm text-slate-500">
        <Link to="/" className="hover:text-brand-600">Home</Link>
        <span className="mx-2">/</span>
        <Link to="/products" className="hover:text-brand-600">Shop</Link>
        <span className="mx-2">/</span>
        <span className="text-slate-800">{product.name}</span>
      </nav>

      <div className="grid gap-10 lg:grid-cols-2">
        <div className="flex aspect-square items-center justify-center rounded-3xl bg-gradient-to-br from-brand-50 via-white to-slate-100 shadow-card">
          <div className="text-center">
            <div className="mx-auto mb-4 flex h-32 w-32 items-center justify-center rounded-3xl bg-brand-100">
              <svg className="h-16 w-16 text-brand-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
            </div>
            <p className="text-sm font-medium text-brand-600">{product.category}</p>
          </div>
        </div>

        <div className="flex flex-col">
          <p className="text-sm font-semibold uppercase tracking-wide text-brand-600">{product.brand}</p>
          <h1 className="mt-2 font-display text-3xl font-bold text-slate-900 sm:text-4xl">{product.name}</h1>
          <p className="mt-4 text-base leading-relaxed text-slate-600">
            {product.description || 'Premium quality products from TechVault.'}
          </p>

          <div className="mt-6 flex items-baseline gap-3">
            <span className="font-display text-3xl font-bold text-slate-900">{formatPrice(product.price)}</span>
            {inStock ? (
              <span className="badge bg-emerald-100 text-emerald-700">{product.stockQuantity} available</span>
            ) : (
              <span className="badge bg-red-100 text-red-700">Out of stock</span>
            )}
          </div>

          {inStock && (
            <div className="mt-8 flex flex-wrap items-center gap-4">
              <div className="flex items-center rounded-xl border border-slate-200 bg-white">
                <button onClick={() => setQty((q) => Math.max(1, q - 1))} className="px-3 py-2 text-lg font-medium text-slate-600 hover:text-brand-600">−</button>
                <span className="w-10 text-center text-sm font-semibold">{qty}</span>
                <button onClick={() => setQty((q) => Math.min(product.stockQuantity, q + 1))} className="px-3 py-2 text-lg font-medium text-slate-600 hover:text-brand-600">+</button>
              </div>
              <button onClick={handleAdd} className={`btn-primary flex-1 sm:flex-none !px-8 ${added ? '!bg-emerald-600' : ''}`}>
                {added ? 'Added to cart ✓' : 'Add to cart'}
              </button>
            </div>
          )}

          <div className="mt-10 grid grid-cols-2 gap-4 border-t border-slate-100 pt-8">
            {[
              { label: 'Brand', value: product.brand },
              { label: 'Category', value: product.category },
              { label: 'Stock', value: inStock ? `${product.stockQuantity} units` : 'Unavailable' },
              { label: 'SKU', value: `TN-${product.id}` },
            ].map((row) => (
              <div key={row.label}>
                <p className="text-xs font-medium uppercase tracking-wide text-slate-400">{row.label}</p>
                <p className="mt-0.5 text-sm font-semibold text-slate-800">{row.value}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
