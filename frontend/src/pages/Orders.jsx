import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getOrdersByCustomer, cancelOrder } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

function formatPrice(price) {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(price));
}

function formatDate(iso) {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('en-ZA', { dateStyle: 'medium', timeStyle: 'short' });
}

const statusStyles = {
    PENDING: 'bg-amber-100 text-amber-800',
    CONFIRMED: 'bg-sky-100 text-sky-800',
    SHIPPED: 'bg-violet-100 text-violet-800',
    DELIVERED: 'bg-emerald-100 text-emerald-800',
    CANCELLED: 'bg-red-100 text-red-800',
};

const progress = { PENDING: 25, CONFIRMED: 50, SHIPPED: 75, DELIVERED: 100 };

const FILTERS = [
    { key: 'ALL', label: 'All' },
    { key: 'ACTIVE', label: 'In progress' },
    { key: 'DELIVERED', label: 'Delivered' },
    { key: 'CANCELLED', label: 'Cancelled' },
];

export default function Orders() {
    const { user } = useAuth();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(!!user?.customerId);
    const [filter, setFilter] = useState('ALL');

    const load = () => {
        if (!user?.customerId) return;
        setLoading(true);
        getOrdersByCustomer(user.customerId)
            .then((data) => setOrders(Array.isArray(data) ? data : []))
            .catch(() => setOrders([]))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        load();
    }, [user?.customerId]);

    const handleCancel = async (id) => {
        if (!window.confirm('Cancel this order?')) return;
        try {
            await cancelOrder(id);
            load();
        } catch (err) {
            alert(err.response?.data?.error || 'Could not cancel order');
        }
    };

    if (!user?.customerId) {
        return (
            <div className="mx-auto max-w-md px-4 py-20 text-center">
                <p className="text-slate-500">Please log in to see your orders.</p>
                <Link to="/login" className="btn-primary mt-4 inline-flex">Log in</Link>
                <div>
                    <Link to="/products" className="mt-4 inline-block text-sm text-brand-600">Continue shopping</Link>
                </div>
            </div>
        );
    }

    if (loading) return <LoadingSpinner label="Loading orders..." />;

    const visible = orders.filter((o) => {
        if (filter === 'ALL') return true;
        if (filter === 'ACTIVE') return ['PENDING', 'CONFIRMED', 'SHIPPED'].includes(o.status);
        return o.status === filter;
    });

    return (
        <div className="mx-auto max-w-5xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
            <h1 className="font-display text-3xl font-bold text-slate-900">My orders</h1>
            <p className="mt-1 text-sm text-slate-500">Select an order to see its items, courier and tracking.</p>

            <div className="mt-6 flex flex-wrap gap-2">
                {FILTERS.map((f) => (
                    <button
                        key={f.key}
                        onClick={() => setFilter(f.key)}
                        className={`rounded-full px-4 py-1.5 text-sm font-medium transition ${
                            filter === f.key
                                ? 'bg-brand-600 text-white shadow-sm'
                                : 'border border-slate-200 bg-white text-slate-600 hover:border-brand-300 hover:text-brand-600'
                        }`}
                    >
                        {f.label}
                    </button>
                ))}
            </div>

            {visible.length === 0 ? (
                <div className="mt-8 rounded-2xl border border-dashed border-slate-200 py-16 text-center">
                    <p className="text-slate-500">
                        {orders.length === 0 ? 'You have no orders yet.' : 'No orders in this view.'}
                    </p>
                    <Link to="/products" className="btn-primary mt-4 inline-flex">Start shopping</Link>
                </div>
            ) : (
                <div className="mt-6 space-y-4">
                    {visible.map((order) => {
                        const items = order.orderItems || [];
                        const cancellable = order.status === 'PENDING' || order.status === 'CONFIRMED';
                        return (
                            <div key={order.id} className="card overflow-hidden">
                                <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 bg-slate-50 px-5 py-3 text-sm">
                                    <div className="flex flex-wrap gap-x-8 gap-y-1">
                                        <div>
                                            <p className="text-[11px] uppercase tracking-wide text-slate-400">Order placed</p>
                                            <p className="font-medium text-slate-700">{formatDate(order.orderDate)}</p>
                                        </div>
                                        <div>
                                            <p className="text-[11px] uppercase tracking-wide text-slate-400">Total</p>
                                            <p className="font-medium text-slate-700">{formatPrice(order.totalAmount)}</p>
                                        </div>
                                        <div>
                                            <p className="text-[11px] uppercase tracking-wide text-slate-400">Order number</p>
                                            <Link to={`/orders/${order.id}`} className="font-semibold text-brand-600 hover:underline">
                                                #{order.id}
                                            </Link>
                                        </div>
                                    </div>
                                    <span className={`badge ${statusStyles[order.status] || 'bg-slate-100 text-slate-700'}`}>
                                        {order.status}
                                    </span>
                                </div>

                                <div className="p-5">
                                    <div className="flex flex-wrap items-center gap-4">
                                        <div className="flex -space-x-2">
                                            {items.slice(0, 4).map((item) => (
                                                <div key={item.id} className="flex h-14 w-14 items-center justify-center overflow-hidden rounded-xl border-2 border-white bg-slate-100">
                                                    {item.product?.imageUrl ? (
                                                        <img src={item.product.imageUrl} alt="" className="h-full w-full object-contain" />
                                                    ) : (
                                                        <span className="text-[10px] text-slate-400">—</span>
                                                    )}
                                                </div>
                                            ))}
                                            {items.length > 4 && (
                                                <div className="flex h-14 w-14 items-center justify-center rounded-xl border-2 border-white bg-slate-200 text-xs font-semibold text-slate-600">
                                                    +{items.length - 4}
                                                </div>
                                            )}
                                        </div>
                                        <div className="min-w-0 flex-1 text-sm text-slate-600">
                                            {items.slice(0, 2).map((item) => (
                                                <p key={item.id} className="truncate">{item.product?.name || 'Product'} × {item.quantity}</p>
                                            ))}
                                            {items.length > 2 && <p className="text-xs text-slate-400">and {items.length - 2} more</p>}
                                        </div>
                                        <Link to={`/orders/${order.id}`} className="btn-primary !px-5 !py-2 text-sm">
                                            Track order
                                        </Link>
                                    </div>

                                    {order.status !== 'CANCELLED' && (
                                        <div className="mt-5">
                                            <div className="h-1.5 overflow-hidden rounded-full bg-slate-100">
                                                <div className="h-full rounded-full bg-gradient-to-r from-brand-500 to-accent-400 transition-all duration-700" style={{ width: `${progress[order.status] || 0}%` }} />
                                            </div>
                                            <div className="mt-1 flex justify-between text-[11px] text-slate-400">
                                                <span>Placed</span><span>Confirmed</span><span>Shipped</span><span>Delivered</span>
                                            </div>
                                        </div>
                                    )}

                                    {cancellable && (
                                        <button onClick={() => handleCancel(order.id)} className="mt-3 text-xs font-medium text-red-600 hover:text-red-700">
                                            Cancel order
                                        </button>
                                    )}
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
}
