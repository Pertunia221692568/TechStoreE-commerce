import { useEffect, useMemo, useState } from 'react';
import { getOrders, getOrderTracking, updateOrderStatus } from '../../services/api';
import LoadingSpinner from '../../components/LoadingSpinner';
import { PageHeader, Modal, Notice, useNotice, Empty, Th, Td, StatusBadge, money, fmtDate } from '../../components/admin/ui';

const STATUSES = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

export default function AdminOrders() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState('ALL');
    const [q, setQ] = useState('');
    const [open, setOpen] = useState(null);       // selected order
    const [tracking, setTracking] = useState(null);
    const [notice, show] = useNotice();

    const load = () =>
        getOrders().then((d) => setOrders((Array.isArray(d) ? d : []).sort((a, b) => b.id - a.id)))
            .catch(() => show('error', 'Could not load orders.')).finally(() => setLoading(false));
    useEffect(() => { load(); }, []);

    const name = (o) => `${o.customer?.firstName || ''} ${o.customer?.lastName || ''}`.trim() || '—';

    const shown = useMemo(() => orders.filter((o) =>
        (filter === 'ALL' || o.status === filter) &&
        `${o.id} ${name(o)} ${o.customer?.email || ''}`.toLowerCase().includes(q.toLowerCase())), [orders, filter, q]);

    const view = (o) => {
        setOpen(o);
        setTracking(null);
        getOrderTracking(o.id).then(setTracking).catch(() => setTracking(null));
    };

    const changeStatus = async (o, status) => {
        if (status === o.status) return;
        try {
            const updated = await updateOrderStatus(o.id, status);
            show('ok', `Order #${o.id} is now ${status}.`);
            setOrders((list) => list.map((x) => (x.id === o.id ? { ...x, ...updated, customer: x.customer, orderItems: x.orderItems } : x)));
            if (open?.id === o.id) { setOpen((x) => ({ ...x, status })); getOrderTracking(o.id).then(setTracking).catch(() => {}); }
        } catch (err) {
            show('error', err.response?.data?.error || 'Could not update the status.');
        }
    };

    if (loading) return <LoadingSpinner label="Loading orders..." />;

    const counts = (s) => (s === 'ALL' ? orders.length : orders.filter((o) => o.status === s).length);

    return (
        <div className="animate-fade-in">
            <PageHeader title="Orders" subtitle="View orders and move them through confirmed, shipped and delivered." />
            <Notice notice={notice} />

            <div className="mb-4 flex flex-wrap items-center gap-2">
                {['ALL', ...STATUSES].map((s) => (
                    <button key={s} onClick={() => setFilter(s)}
                            className={`rounded-full px-3.5 py-1.5 text-xs font-semibold transition ${filter === s ? 'bg-brand-600 text-white' : 'border border-slate-200 bg-white text-slate-600 hover:border-brand-300'}`}>
                        {s === 'ALL' ? 'All' : s.charAt(0) + s.slice(1).toLowerCase()} ({counts(s)})
                    </button>
                ))}
                <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search order #, name or email..." className="input-field ml-auto max-w-xs" />
            </div>

            {shown.length === 0 ? <Empty text="No orders in this view." /> : (
                <div className="card overflow-x-auto">
                    <table className="w-full text-sm">
                        <thead className="border-b border-slate-100 bg-slate-50"><tr>
                            <Th>Order</Th><Th>Customer</Th><Th>Date</Th><Th>Items</Th><Th>Total</Th><Th>Status</Th><Th className="text-right">Actions</Th>
                        </tr></thead>
                        <tbody className="divide-y divide-slate-100">
                        {shown.map((o) => (
                            <tr key={o.id} className="hover:bg-slate-50/60">
                                <Td className="font-semibold text-slate-900">#{o.id}</Td>
                                <Td><p className="font-medium text-slate-800">{name(o)}</p><p className="text-xs text-slate-400">{o.customer?.email}</p></Td>
                                <Td className="text-slate-500">{fmtDate(o.orderDate)}</Td>
                                <Td>{(o.orderItems || []).reduce((n, i) => n + i.quantity, 0)}</Td>
                                <Td className="font-medium">{money(o.totalAmount)}</Td>
                                <Td>
                                    <select value={o.status} onChange={(e) => changeStatus(o, e.target.value)}
                                            className="rounded-lg border border-slate-200 bg-white px-2 py-1 text-xs font-semibold">
                                        {STATUSES.map((s) => <option key={s}>{s}</option>)}
                                    </select>
                                </Td>
                                <Td className="text-right"><button onClick={() => view(o)} className="rounded-lg border border-slate-200 px-3 py-1 text-xs font-semibold text-slate-700 hover:bg-slate-50">View</button></Td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {open && (
                <Modal title={`Order #${open.id}`} onClose={() => setOpen(null)} wide>
                    <div className="grid gap-4 sm:grid-cols-2 text-sm">
                        <div>
                            <p className="text-xs uppercase tracking-wide text-slate-400">Customer</p>
                            <p className="font-semibold text-slate-900">{name(open)}</p>
                            <p className="text-slate-500">{open.customer?.email}</p>
                            <p className="text-slate-500">{open.customer?.phone}</p>
                        </div>
                        <div>
                            <p className="text-xs uppercase tracking-wide text-slate-400">Ship to</p>
                            <p className="text-slate-800">{open.shippingAddress || '—'}</p>
                            <p className="mt-1 text-xs text-slate-400">Placed {fmtDate(open.orderDate)}</p>
                        </div>
                    </div>

                    <ul className="mt-4 divide-y divide-slate-100 rounded-xl border border-slate-100">
                        {(open.orderItems || []).map((i) => (
                            <li key={i.id} className="flex items-center gap-3 p-3 text-sm">
                                <div className="flex h-12 w-12 shrink-0 items-center justify-center overflow-hidden rounded-lg bg-slate-100">
                                    {i.product?.imageUrl && <img src={i.product.imageUrl} alt="" className="h-full w-full object-contain" />}
                                </div>
                                <div className="min-w-0 flex-1">
                                    <p className="truncate font-semibold text-slate-900">{i.product?.name}</p>
                                    <p className="text-xs text-slate-400">Qty {i.quantity} × {money(i.unitPrice)}</p>
                                </div>
                                <p className="font-semibold">{money(i.unitPrice * i.quantity)}</p>
                            </li>
                        ))}
                        <li className="flex justify-between p-3 font-display font-bold"><span>Total</span><span>{money(open.totalAmount)}</span></li>
                    </ul>

                    <div className="mt-4 grid gap-4 sm:grid-cols-2 text-sm">
                        <div className="rounded-xl bg-slate-50 p-4">
                            <p className="text-xs uppercase tracking-wide text-slate-400">Courier</p>
                            {tracking?.delivery ? (
                                <>
                                    <p className="font-semibold text-slate-900">{tracking.delivery.carrier}</p>
                                    <p className="font-mono text-xs text-slate-500">{tracking.delivery.trackingNumber}</p>
                                    <p className="mt-1 text-xs text-slate-500">ETA {fmtDate(tracking.delivery.estimatedDeliveryDate, false)}</p>
                                </>
                            ) : <p className="text-slate-400">Loading…</p>}
                        </div>
                        <div className="rounded-xl bg-slate-50 p-4">
                            <p className="text-xs uppercase tracking-wide text-slate-400">Payment</p>
                            {tracking?.payment ? (
                                <>
                                    <p className="font-semibold text-slate-900">
                                        {tracking.payment.paymentMethod === 'Card' && tracking.payment.cardLast4 ? `Card ending ${tracking.payment.cardLast4}` : tracking.payment.paymentMethod}
                                    </p>
                                    <p className="text-xs text-slate-500">{money(tracking.payment.amount)} · {tracking.payment.status}</p>
                                </>
                            ) : <p className="text-slate-400">No payment recorded</p>}
                        </div>
                    </div>

                    <div className="mt-5 flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 pt-4">
                        <div className="flex items-center gap-2 text-sm"><span className="text-slate-500">Status</span><StatusBadge status={open.status} /></div>
                        <div className="flex items-center gap-2">
                            <span className="text-sm text-slate-500">Update to</span>
                            <select value={open.status} onChange={(e) => changeStatus(open, e.target.value)} className="input-field !w-auto !py-1.5">
                                {STATUSES.map((s) => <option key={s}>{s}</option>)}
                            </select>
                        </div>
                    </div>
                </Modal>
            )}
        </div>
    );
}
