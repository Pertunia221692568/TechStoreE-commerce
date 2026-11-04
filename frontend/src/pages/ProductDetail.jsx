import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getProduct, getRatingsByProduct, createRating } from '../services/api';
import { useCart } from '../context/CartContext';
import LoadingSpinner from '../components/LoadingSpinner';

function formatPrice(price) {
    return new Intl.NumberFormat('en-ZA', {
        style: 'currency',
        currency: 'ZAR',
    }).format(Number(price));
}

function Stars({ value, size = 'md', onSelect }) {
    const cls = size === 'lg' ? 'h-8 w-8' : size === 'sm' ? 'h-3 w-3' : 'h-4 w-4';
    return (
        <div className="flex gap-0.5">
            {[1, 2, 3, 4, 5].map((n) => (
                <button
                    key={n}
                    type="button"
                    disabled={!onSelect}
                    onClick={() => onSelect && onSelect(n)}
                    className={onSelect ? 'rounded p-0.5 transition hover:scale-110' : ''}
                    aria-label={`${n} stars`}
                >
                    <svg
                        className={`${cls} ${n <= value ? 'fill-amber-400 text-amber-400' : 'fill-slate-200 text-slate-200'}`}
                        viewBox="0 0 20 20"
                    >
                        <path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z" />
                    </svg>
                </button>
            ))}
        </div>
    );
}

export default function ProductDetail() {
    const { id } = useParams();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [qty, setQty] = useState(1);
    const [added, setAdded] = useState(false);
    const [imgFailed, setImgFailed] = useState(false);
    const { addToCart } = useCart();

    const [ratings, setRatings] = useState([]);
    const [score, setScore] = useState(5);
    const [comment, setComment] = useState('');
    const [ratingMsg, setRatingMsg] = useState('');
    const [ratingLoading, setRatingLoading] = useState(false);

    const loadRatings = () => {
        if (!id) return;
        getRatingsByProduct(id)
            .then((data) => setRatings(Array.isArray(data) ? data : []))
            .catch(() => setRatings([]));
    };

    useEffect(() => {
        setLoading(true);
        setImgFailed(false);
        getProduct(id)
            .then(setProduct)
            .catch(() => setProduct(null))
            .finally(() => setLoading(false));
        loadRatings();
    }, [id]);

    const handleSubmitRating = async (e) => {
        e.preventDefault();
        setRatingLoading(true);
        setRatingMsg('');
        try {
            const now = new Date();
            const pad = (n) => String(n).padStart(2, '0');
            const ratingDate = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;

            await createRating({
                score: Number(score),
                comment: comment.trim() || 'Great product!',
                ratingDate,
                productId: Number(id),
            });
            setComment('');
            setScore(5);
            setRatingMsg('Thank you for your rating!');
            loadRatings();
        } catch {
            setRatingMsg('Could not save your rating. Please try again.');
        } finally {
            setRatingLoading(false);
        }
    };

    if (loading) return <LoadingSpinner />;
    if (!product) {
        return (
            <div className="mx-auto max-w-7xl px-4 py-20 text-center">
                <p className="text-slate-500">Product not found.</p>
                <Link to="/products" className="btn-primary mt-4 inline-flex">
                    Back to shop
                </Link>
            </div>
        );
    }

    const imgSrc = product.imageUrl || product.image || '';
    const inStock = (product.stockQuantity ?? product.stock ?? 0) > 0;
    const recentRatings = [...ratings]
        .sort((a, b) => new Date(b.ratingDate || 0) - new Date(a.ratingDate || 0))
        .slice(0, 20);
    const avg =
        recentRatings.length > 0
            ? recentRatings.reduce((s, r) => s + (Number(r.score) || 0), 0) / recentRatings.length
            : 0;

    const handleAdd = () => {
        addToCart(product, qty);
        setAdded(true);
        setTimeout(() => setAdded(false), 2000);
    };

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
            <Link to="/products" className="text-sm font-medium text-brand-600 hover:text-brand-700">
                ← Back to shop
            </Link>

            <div className="mt-6 grid gap-10 lg:grid-cols-2">
                <div className="card overflow-hidden">
                    {imgSrc && !imgFailed ? (
                        <img
                            src={imgSrc}
                            alt={product.name}
                            className="aspect-square w-full object-cover"
                            onError={() => setImgFailed(true)}
                        />
                    ) : (
                        <div className="flex aspect-square items-center justify-center bg-slate-100 text-slate-400">
                            No image
                        </div>
                    )}
                </div>

                <div>
                    <p className="text-sm font-medium text-brand-600">{product.brand || product.category}</p>
                    <h1 className="mt-1 font-display text-3xl font-bold text-slate-900">{product.name}</h1>
                    <p className="mt-4 font-display text-2xl font-bold text-slate-900">
                        {formatPrice(product.price)}
                    </p>
                    <p className="mt-4 text-sm leading-relaxed text-slate-600">
                        {product.description || 'No description available.'}
                    </p>

                    <div className="mt-6 grid grid-cols-2 gap-4 rounded-2xl bg-slate-50 p-4 sm:grid-cols-3">
                        {[
                            { label: 'Category', value: product.category || '—' },
                            { label: 'Condition', value: product.condition || '—' },
                            {
                                label: 'Stock',
                                value: inStock
                                    ? `${product.stockQuantity ?? product.stock} available`
                                    : 'Out of stock',
                            },
                        ].map((row) => (
                            <div key={row.label}>
                                <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
                                    {row.label}
                                </p>
                                <p className="mt-0.5 text-sm font-semibold text-slate-800">{row.value}</p>
                            </div>
                        ))}
                    </div>

                    <div className="mt-8 flex flex-wrap items-center gap-4">
                        <div className="flex items-center rounded-xl border border-slate-200">
                            <button
                                type="button"
                                className="px-3 py-2 text-slate-600 hover:bg-slate-50"
                                onClick={() => setQty((q) => Math.max(1, q - 1))}
                            >
                                −
                            </button>
                            <span className="w-10 text-center text-sm font-semibold">{qty}</span>
                            <button
                                type="button"
                                className="px-3 py-2 text-slate-600 hover:bg-slate-50"
                                onClick={() => setQty((q) => q + 1)}
                            >
                                +
                            </button>
                        </div>
                        <button
                            type="button"
                            disabled={!inStock}
                            onClick={handleAdd}
                            className="btn-primary"
                        >
                            {added ? 'Added!' : inStock ? 'Add to cart' : 'Out of stock'}
                        </button>
                    </div>
                </div>
            </div>

            {/* Per-product reviews */}
            <section className="mt-14 border-t border-slate-200 pt-10">
                <div className="mb-6 flex flex-wrap items-end justify-between gap-4">
                    <div>
                        <h2 className="font-display text-xl font-bold text-slate-900">
                            Customer reviews
                        </h2>
                        <p className="mt-1 text-sm text-slate-500">
                            {recentRatings.length === 0
                                ? 'No reviews yet for this product'
                                : `${recentRatings.length} review${recentRatings.length === 1 ? '' : 's'} for this product`}
                        </p>
                    </div>
                    {recentRatings.length > 0 && (
                        <div className="flex items-center gap-2 rounded-xl bg-brand-50 px-4 py-2">
                            <span className="font-display text-2xl font-bold text-brand-700">
                                {avg.toFixed(1)}
                            </span>
                            <Stars value={Math.round(avg)} size="sm" />
                        </div>
                    )}
                </div>

                <div className="grid gap-8 lg:grid-cols-5">
                    <form
                        onSubmit={handleSubmitRating}
                        className="card space-y-4 p-6 lg:col-span-2"
                    >
                        <h3 className="text-sm font-semibold text-slate-800">Write a review</h3>
                        <div>
                            <label className="mb-2 block text-sm font-medium text-slate-700">
                                Your score
                            </label>
                            <Stars value={score} size="lg" onSelect={setScore} />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">
                                Comment
                            </label>
                            <textarea
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                                rows={3}
                                placeholder="What did you like or dislike about this product?"
                                className="input-field"
                            />
                        </div>
                        {ratingMsg && (
                            <p
                                className={`text-sm ${
                                    ratingMsg.includes('Thank') ? 'text-emerald-600' : 'text-red-600'
                                }`}
                            >
                                {ratingMsg}
                            </p>
                        )}
                        <button type="submit" disabled={ratingLoading} className="btn-primary w-full">
                            {ratingLoading ? 'Submitting…' : 'Submit review'}
                        </button>
                    </form>

                    <ul className="space-y-3 lg:col-span-3">
                        {recentRatings.length === 0 && (
                            <li className="rounded-2xl border border-dashed border-slate-200 py-10 text-center text-sm text-slate-400">
                                No reviews yet. Be the first to review this product.
                            </li>
                        )}
                        {recentRatings.map((r) => (
                            <li key={r.id} className="card flex items-start gap-4 p-4">
                                <div className="flex shrink-0 flex-col items-center gap-1">
                                    <span className="font-display text-lg font-bold text-brand-700">
                                        {r.score}
                                    </span>
                                    <Stars value={r.score || 0} size="sm" />
                                </div>
                                <div className="min-w-0 flex-1">
                                    <p className="text-sm text-slate-700">
                                        {r.comment || 'No comment'}
                                    </p>
                                    <p className="mt-1 text-xs text-slate-400">
                                        {r.ratingDate
                                            ? new Date(r.ratingDate).toLocaleString('en-ZA', {
                                                dateStyle: 'medium',
                                                timeStyle: 'short',
                                            })
                                            : ''}
                                    </p>
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
            </section>
        </div>
    );
}