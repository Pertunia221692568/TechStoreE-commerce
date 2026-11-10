import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { getOrder, getOrderTracking, cancelOrder } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

const STEPS = [
    { key: 'PENDING', label: 'Order placed', text: 'We have received your order.' },
    { key: 'CONFIRMED', label: 'Confirmed', text: 'Your order is confirmed and being prepared.' },
    { key: 'SHIPPED', label: 'Shipped', text: 'Your order is with the courier and on its way.' },
    { key: 'DELIVERED', label: 'Delivered', text: 'Your order has been delivered. Enjoy!' },
];

const statusStyles = {
    PENDING: 'bg-amber-100 text-amber-800',
    CONFIRMED: 'bg-sky-100 text-sky-800',
    SHIPPED: 'bg-violet-100 text-violet-800',
    DELIVERED: 'bg-emerald-100 text-emerald-800',
    CANCELLED: 'bg-red-100 text-red-800',
};

const deliveryLabels = {
    PENDING: 'Waiting for confirmation',
    CONFIRMED: 'Courier assigned',
    DISPATCHED: 'Handed to courier',
    IN_TRANSIT: 'In transit',
    DELIVERED: 'Delivered',
    CANCELLED: 'Cancelled',
};

function formatPrice(price) {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(price));
}

function formatDate(iso, withTime = true) {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('en-ZA', withTime ? { dateStyle: 'medium', timeStyle: 'short' } : { dateStyle: 'full' });
}

export default function OrderDetail() {
    const { id } = useParams();
    const { user } = useAuth();
    const { addToCart } = useCart();
    const [order, setOrder] = useState(null);
    const [tracking, setTracking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [notFound, setNotFound] = useState(false);
    const [copied, setCopied] = useState(false);
    const [error, setError] = useState('');

    const load = () => {
        setLoading(true);
        Promise.all([getOrder(id), getOrderTracking(id).catch(() => null)])
            .then(([o, t]) => {
                if (!user?.customerId || o?.customer?.id !== user.customerId) {
                    setNotFound(true);
                } else {
                    setOrder(o);
                    setTracking(t);
                }
            })
            .catch(() => setNotFound(true))
            .finally(() => setLoading(false));
    };

    useEffect(load, [id, user?.customerId]);

    if (!user?.customerId) {
        return (
            <div className="mx-auto max-w-md px-4 py-20 text-center">
                <p className="text-slate-500">Please log in to see your order.</p>
                <Link to="/login" className="btn-primary mt-4 inline-flex">Log in</Link>
            </div>
        );
    }
    if (loading) return <LoadingSpinner label="Loading your order..." />;
    if (notFound || !order) {
        return (
            <div className="mx-auto max-w-md px-4 py-20 text-center">
                <p className="font-display text-xl font-bold text-slate-900">Order not found</p>
                <p className="mt-2 text-sm text-slate-500">We couldn’t find that order on your account.</p>
                <Link to="/orders" className="btn-primary mt-4 inline-flex">Back to my orders</Link>
            </div>
        );
    }

    const delivery = tracking?.delivery;
    const payment = tracking?.payment;
    const cancelled = order.status === 'CANCELLED';
    const stepIndex = STEPS.findIndex((s) => s.key === order.status);
    const canCancel = order.status === 'PENDING' || order.status === 'CONFIRMED';
    const itemCount = (order.orderItems || []).reduce((n, i) => n + i.quantity, 0);

    const handleCancel = async () => {
        if (!window.confirm('Cancel this order?')) return;
        try {
            await cancelOrder(order.id);
            load();
        } catch (err) {
            setError(err.response?.data?.error || 'Could not cancel the order.');
        }
    };

    const copyTracking = () => {
        if (!delivery?.trackingNumber) return;
        navigator.clipboard?.writeText(delivery.trackingNumber);
        setCopied(true);
        setTimeout(() => setCopied(false), 1500);
    };

    const buyAgain = () => {
        (order.orderItems || []).forEach((i) => i.product && addToCart(i.product, i.quantity));
    };

    return (
        <div className="mx-auto max-w-5xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
            <Link to="/orders" className="text-sm font-medium text-brand-600 hover:underline">← My orders</Link>

            {/* Header */}
            <div className="mt-4 flex flex-wrap items-start justify-between gap-4">
                <div>
                    <h1 className="font-display text-3xl font-bold text-slate-900">Order #{order.id}</h1>
                    <p className="mt-1 text-sm text-slate-500">
                        Placed on {formatDate(order.orderDate)} · {itemCount} item{itemCount === 1 ? '' : 's'}
                    </p>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                    <span className={`badge !px-3 !py-1 text-sm ${statusStyles[order.status] || 'bg-slate-100 text-slate-700'}`}>
                        {order.status}
                    </span>
                    <button onClick={() => window.print()} className="btn-secondary !px-4 !py-2 text-sm print:hidden">Print</button>
                    <Link to="/cart" onClick={buyAgain} className="btn-secondary !px-4 !py-2 text-sm print:hidden">Buy again</Link>
                </div>
            </div>

            {error && <p className="mt-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}

            {/* Tracker */}
            <section className="card mt-6 p-6">
                <h2 className="font-display text-lg font-bold text-slate-900">Order status</h2>
                {cancelled ? (
                    <div className="mt-4 rounded-xl border border-red-200 bg-red-50 p-4">
                        <p className="font-semibold text-red-800">This order was cancelled</p>
                        <p className="mt-1 text-sm text-red-700">
                            Stock was returned and you were not charged for cancelled items. Need help? <Link to="/contact" className="underline">Contact us</Link>.
                        </p>
                    </div>
                ) : (
                    <>
                        <ol className="mt-6 grid grid-cols-4">
                            {STEPS.map((step, i) => {
                                const done = i <= stepIndex;
                                const current = i === stepIndex;
                                return (
                                    <li key={step.key} className="relative flex flex-col items-center text-center">
                                        {i > 0 && (
                                            <span className={`absolute right-1/2 top-4 h-1 w-full -translate-y-1/2 rounded ${i <= stepIndex ? 'bg-brand-600' : 'bg-slate-200'}`} />
                                        )}
                                        <span className={`relative z-10 flex h-8 w-8 items-center justify-center rounded-full text-sm font-bold ${
                                            done ? 'bg-brand-600 text-white' : 'bg-slate-200 text-slate-400'
                                        } ${current ? 'ring-4 ring-brand-200' : ''}`}>
                                            {done ? '✓' : i + 1}
                                        </span>
                                        <span className={`mt-2 text-xs font-semibold sm:text-sm ${done ? 'text-slate-900' : 'text-slate-400'}`}>
                                            {step.label}
                                        </span>
                                    </li>
                                );
                            })}
                        </ol>
                        <p className="mt-6 rounded-xl bg-brand-50 px-4 py-3 text-sm text-brand-900">
                            {STEPS[stepIndex]?.text}
                            {order.status !== 'DELIVERED' && delivery?.estimatedDeliveryDate && (
                                <> Estimated delivery: <strong>{formatDate(delivery.estimatedDeliveryDate, false)}</strong>.</>
                            )}
                        </p>
                    </>
                )}
            </section>

            <div className="mt-6 grid gap-6 lg:grid-cols-3">
                <div className="space-y-6 lg:col-span-2">
                    {/* Items */}
                    <section className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">Items in this order</h2>
                        <ul className="mt-4 divide-y divide-slate-100">
                            {(order.orderItems || []).map((item) => (
                                <li key={item.id} className="flex gap-4 py-4 first:pt-0 last:pb-0">
                                    <Link to={`/products/${item.product?.id}`} className="flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-xl bg-slate-100">
                                        {item.product?.imageUrl ? (
                                            <img src={item.product.imageUrl} alt={item.product.name} className="h-full w-full object-contain" />
                                        ) : (
                                            <span className="text-xs text-slate-400">No image</span>
                                        )}
                                    </Link>
                                    <div className="min-w-0 flex-1">
                                        <p className="text-xs uppercase tracking-wide text-slate-400">{item.product?.brand}</p>
                                        <Link to={`/products/${item.product?.id}`} className="font-semibold text-slate-900 hover:text-brand-700">
                                            {item.product?.name || 'Product'}
                                        </Link>
                                        {item.product?.itemCondition && (
                                            <p className="text-xs text-slate-500">Condition: {item.product.itemCondition}</p>
                                        )}
                                        <p className="mt-1 text-sm text-slate-500">
                                            Qty {item.quantity} × {formatPrice(item.unitPrice)}
                                        </p>
                                    </div>
                                    <p className="font-display font-bold text-slate-900">{formatPrice(item.unitPrice * item.quantity)}</p>
                                </li>
                            ))}
                        </ul>
                    </section>

                    {/* Courier */}
                    <section className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">Courier & delivery</h2>
                        {delivery ? (
                            <dl className="mt-4 grid gap-x-6 gap-y-4 text-sm sm:grid-cols-2">
                                <div>
                                    <dt className="text-xs uppercase tracking-wide text-slate-400">Courier</dt>
                                    <dd className="mt-1 font-semibold text-slate-900">{delivery.carrier}</dd>
                                </div>
                                <div>
                                    <dt className="text-xs uppercase tracking-wide text-slate-400">Delivery status</dt>
                                    <dd className="mt-1 font-semibold text-slate-900">{deliveryLabels[delivery.status] || delivery.status}</dd>
                                </div>
                                <div>
                                    <dt className="text-xs uppercase tracking-wide text-slate-400">Tracking number</dt>
                                    <dd className="mt-1 flex items-center gap-2 font-mono text-slate-900">
                                        {delivery.trackingNumber}
                                        <button onClick={copyTracking} className="rounded-md border border-slate-200 px-2 py-0.5 font-sans text-xs text-slate-600 hover:bg-slate-50 print:hidden">
                                            {copied ? 'Copied ✓' : 'Copy'}
                                        </button>
                                    </dd>
                                </div>
                                <div>
                                    <dt className="text-xs uppercase tracking-wide text-slate-400">Shipping ID</dt>
                                    <dd className="mt-1 font-mono text-slate-900">{delivery.shippingId}</dd>
                                </div>
                                <div className="sm:col-span-2">
                                    <dt className="text-xs uppercase tracking-wide text-slate-400">
                                        {delivery.actualDeliveryDate ? 'Delivered on' : 'Estimated delivery'}
                                    </dt>
                                    <dd className="mt-1 font-semibold text-slate-900">
                                        {formatDate(delivery.actualDeliveryDate || delivery.estimatedDeliveryDate, false)}
                                    </dd>
                                </div>
                            </dl>
                        ) : (
                            <p className="mt-3 text-sm text-slate-500">A courier will be assigned once your order is confirmed.</p>
                        )}
                    </section>
                </div>

                {/* Right column */}
                <div className="space-y-6">
                    <section className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">Order summary</h2>
                        <dl className="mt-4 space-y-2 text-sm">
                            <div className="flex justify-between text-slate-600">
                                <dt>Items ({itemCount})</dt>
                                <dd>{formatPrice(order.totalAmount)}</dd>
                            </div>
                            <div className="flex justify-between border-t border-slate-100 pt-3 font-display text-lg font-bold text-slate-900">
                                <dt>Total</dt>
                                <dd>{formatPrice(order.totalAmount)}</dd>
                            </div>
                        </dl>
                    </section>

                    <section className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">Payment</h2>
                        {payment ? (
                            <p className="mt-3 text-sm text-slate-600">
                                {payment.paymentMethod === 'Card' && payment.cardLast4
                                    ? `Card ending ${payment.cardLast4}`
                                    : payment.paymentMethod}
                                <span className="mt-1 block text-xs text-slate-400">Paid {formatDate(payment.paidAt)}</span>
                            </p>
                        ) : (
                            <p className="mt-3 text-sm text-slate-500">No payment details recorded.</p>
                        )}
                    </section>

                    <section className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">Delivery address</h2>
                        <p className="mt-3 text-sm text-slate-600">
                            {order.customer?.firstName} {order.customer?.lastName}
                            <span className="block">{order.shippingAddress || '—'}</span>
                        </p>
                    </section>

                    <section className="card p-6 print:hidden">
                        <h2 className="font-display text-lg font-bold text-slate-900">Need help?</h2>
                        <div className="mt-3 flex flex-col gap-2 text-sm">
                            <Link to="/returns" className="text-brand-600 hover:underline">Returns & refunds</Link>
                            <Link to="/shipping" className="text-brand-600 hover:underline">Shipping information</Link>
                            <Link to="/contact" className="text-brand-600 hover:underline">Contact support</Link>
                        </div>
                        {canCancel && (
                            <button onClick={handleCancel} className="mt-4 w-full rounded-xl border border-red-200 px-4 py-2 text-sm font-semibold text-red-600 hover:bg-red-50">
                                Cancel order
                            </button>
                        )}
                    </section>
                </div>
            </div>
        </div>
    );
}
