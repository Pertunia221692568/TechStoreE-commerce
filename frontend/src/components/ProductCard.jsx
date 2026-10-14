import { Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useState } from 'react';

const categoryColors = {
    Laptop: 'bg-violet-100 text-violet-700',
    Phone: 'bg-sky-100 text-sky-700',
    Mouse: 'bg-emerald-100 text-emerald-700',
    Keyboard: 'bg-amber-100 text-amber-700',
    Monitor: 'bg-rose-100 text-rose-700',
    Desktop: 'bg-indigo-100 text-indigo-700',
    Accessory: 'bg-cyan-100 text-cyan-700',
    General: 'bg-slate-100 text-slate-700',
};

const productIcons = {
    Laptop: (
        <svg className="h-12 w-12 text-brand-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
    ),
    Phone: (
        <svg className="h-12 w-12 text-brand-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" />
        </svg>
    ),
    default: (
        <svg className="h-12 w-12 text-brand-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
        </svg>
    ),
};

function formatPrice(price) {
    return new Intl.NumberFormat('en-ZA', {
        style: 'currency',
        currency: 'ZAR',
        minimumFractionDigits: 2,
    }).format(Number(price));
}

export default function ProductCard({ product }) {
    const { addToCart } = useCart();
    const [added, setAdded] = useState(false);
    const inStock = product.stockQuantity > 0;
    const badgeClass = categoryColors[product.category] || categoryColors.General;
    const icon = productIcons[product.category] || productIcons.default;
    const [imgFailed, setImgFailed] = useState(false);
    const showImage = product.imageUrl && !imgFailed;

    const handleAdd = (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (!inStock) return;
        addToCart(product, 1);
        setAdded(true);
        setTimeout(() => setAdded(false), 1500);
    };

    return (
        <Link
            to={`/products/${product.id}`}
            className="card group flex flex-col overflow-hidden animate-fade-in"
        >
            {/* Image / icon area */}
            <div className="relative flex h-44 items-center justify-center bg-gradient-to-br from-brand-50 to-slate-100 overflow-hidden">
                <div className="absolute inset-0 bg-[radial-gradient(circle_at_30%_20%,rgba(92,124,250,0.12),transparent_60%)]" />
                {showImage ? (
                    <img
                        src={product.imageUrl}
                        alt={product.name}
                        onError={() => setImgFailed(true)}
                        className="relative h-full w-full object-contain p-3 transition-transform duration-300 group-hover:scale-105"
                    />
                ) : (
                    <div className="relative transition-transform duration-300 group-hover:scale-110">
                        {icon}
                    </div>
                )}
                <span className={`badge absolute left-3 top-3 ${badgeClass}`}>
          {product.category || 'General'}
        </span>
                {!inStock ? (
                    <span className="badge absolute right-3 top-3 bg-red-100 text-red-700">
            Out of stock
          </span>
                ) : product.itemCondition ? (
                    <span className={`badge absolute right-3 top-3 ${
                        product.itemCondition === 'New' ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700'
                    }`}>
            {product.itemCondition}
          </span>
                ) : null}
            </div>

            {/* Content */}
            <div className="flex flex-1 flex-col p-4">
                <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
                    {product.brand}
                </p>
                <h3 className="mt-1 line-clamp-2 text-sm font-semibold text-slate-900 group-hover:text-brand-700 transition">
                    {product.name}
                </h3>
                <p className="mt-1 line-clamp-2 text-xs text-slate-500 flex-1">
                    {product.description || 'Premium quality product'}
                </p>

                <div className="mt-3 flex items-end justify-between gap-2">
                    <div>
                        <p className="font-display text-lg font-bold text-slate-900">
                            {formatPrice(product.price)}
                        </p>
                        {inStock && (
                            <p className="text-[11px] text-emerald-600 font-medium">
                                {product.stockQuantity} in stock
                            </p>
                        )}
                    </div>
                    <button
                        onClick={handleAdd}
                        disabled={!inStock}
                        className={`rounded-xl px-3 py-2 text-xs font-semibold transition-all ${
                            added
                                ? 'bg-emerald-500 text-white'
                                : inStock
                                    ? 'bg-brand-600 text-white hover:bg-brand-700 shadow-sm'
                                    : 'bg-slate-100 text-slate-400 cursor-not-allowed'
                        }`}
                    >
                        {added ? 'Added ✓' : 'Add'}
                    </button>
                </div>
            </div>
        </Link>
    );
}
