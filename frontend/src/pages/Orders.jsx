import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getOrdersByCustomer, cancelOrder, getCustomerByEmail } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

function formatPrice(price) {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(price));
}

function formatDate(iso) {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('en-ZA', {
        dateStyle: 'medium',
        timeStyle: 'short',
    });
}

const statusStyles = {
    PENDING: 'bg-amber-100 text-amber-800',
    CONFIRMED: 'bg-sky-100 text-sky-800',
    SHIPPED: 'bg-violet-100 text-violet-800',
    DELIVERED: 'bg-emerald-100 text-emerald-800',
    CANCELLED: 'bg-red-100 text-red-800',
};

export default function Orders() {
    const { user, login } = useAuth();
    const [lookupEmail, setLookupEmail] = useState('');
    const [lookupError, setLookupError] = useState('');
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(!!user?.id);

    const load = () => {
        if (!user?.id) return;
        setLoading(true);
        getOrdersByCustomer(user.id)
            .then((data) => setOrders(Array.isArray(data) ? data : []))
            .catch(() => setOrders([]))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        load();
    }, [user?.id]);

    const handleCancel = async (id) => {
        if (!window.confirm('Cancel this order?')) return;
        try {
            await cancelOrder(id);
            load();
        } catch (err) {
            alert(err.response?.data?.error || 'Could not cancel order');
        }
    };

    const handleLookup = async (e) => {
        e.preventDefault();
        setLookupError('');
        try {
            const customer = await getCustomerByEmail(lookupEmail);
            login(customer);
        } catch {
            setLookupError('No orders found for that email.');
        }
    };

    if (!user?.id) {
        return (
            <div className="mx-auto max-w-md px-4 py-20 text-center">
                <p className="text-slate-500">Enter the email you used at checkout to see your orders.</p>
                <form onSubmit={handleLookup} className="mt-4 space-y-3">
                    <input
                        type="email"
                        value={lookupEmail}
                        onChange={(e) => setLookupEmail(e.target.value)}
                        placeholder="Email"
                        className="input-field"
                        required
                    />
                    {lookupError && <p className="text-sm text-red-600">{lookupError}</p>}
                    <button type="submit" className="btn-primary w-full">Find my orders</button>
                </form>
                <Link to="/products" className="mt-4 inline-block text-sm text-brand-600">Continue shopping</Link>
            </div>
        );
    }

    if (loading) return <LoadingSpinner label="Loading orders..." />;

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
            <h1 className="mb-8 font-display text-3xl font-bold text-slate-900">My orders</h1>

            {orders.length === 0 ? (
                <div className="rounded-2xl border border-dashed border-slate-200 py-16 text-center">
                    <p className="text-slate-500">You have no orders yet.</p>
                    <Link to="/products" className="btn-primary mt-4 inline-flex">Start shopping</Link>
                </div>
            ) : (
                <div className="space-y-4">
                    {orders.map((order) => (
                        <div key={order.id} className="card p-5">
                            <div className="flex flex-wrap items-start justify-between gap-3">
                                <div>
                                    <p className="text-sm font-semibold text-slate-900">Order #{order.id}</p>
                                    <p className="text-xs text-slate-400">{formatDate(order.orderDate)}</p>
                                </div>
                                <span className={`badge ${statusStyles[order.status] || 'bg-slate-100 text-slate-700'}`}>
                  {order.status}
                </span>
                            </div>

                            <div className="mt-4 space-y-2">
                                {(order.orderItems || []).map((item) => (
                                    <div key={item.id} className="flex justify-between text-sm">
                    <span className="text-slate-600">
                      {item.product?.name || 'Product'} × {item.quantity}
                    </span>
                                        <span className="font-medium">{formatPrice(item.unitPrice * item.quantity)}</span>
                                    </div>
                                ))}
                            </div>

                            <div className="mt-4 flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 pt-4">
                                <div>
                                    <p className="text-xs text-slate-400">Shipping</p>
                                    <p className="text-sm text-slate-700">{order.shippingAddress || '—'}</p>
                                </div>
                                <div className="text-right">
                                    <p className="text-xs text-slate-400">Total</p>
                                    <p className="font-display text-lg font-bold text-slate-900">{formatPrice(order.totalAmount)}</p>
                                </div>
                            </div>

                            {(order.status === 'PENDING' || order.status === 'CONFIRMED') && (
                                <button
                                    onClick={() => handleCancel(order.id)}
                                    className="mt-3 text-xs font-medium text-red-600 hover:text-red-700"
                                >
                                    Cancel order
                                </button>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
