import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { adminStats } from '../../services/api';
import LoadingSpinner from '../../components/LoadingSpinner';
import { StatCard, StatusBadge, money, fmtDate, ICONS } from '../../components/admin/ui';

const STATUS_COLORS = {
    PENDING: '#f59e0b', CONFIRMED: '#0ea5e9', SHIPPED: '#8b5cf6', DELIVERED: '#10b981', CANCELLED: '#ef4444',
};

function SalesChart({ data }) {
    const max = Math.max(...data.map((d) => Number(d.revenue)), 1);
    const w = 460, h = 170, pad = 28, bw = 34;
    const gap = (w - pad * 2 - bw * data.length) / (data.length - 1 || 1);
    return (
        <svg viewBox={`0 0 ${w} ${h + 24}`} className="w-full">
            <defs>
                <linearGradient id="bar" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0" stopColor="#22d3ee" />
                    <stop offset="1" stopColor="#4c6ef5" />
                </linearGradient>
            </defs>
            {[0, 0.5, 1].map((t) => (
                <line key={t} x1={pad} x2={w - pad} y1={h - t * (h - 20)} y2={h - t * (h - 20)} stroke="#e2e8f0" strokeDasharray="4 4" />
            ))}
            {data.map((d, i) => {
                const bh = (Number(d.revenue) / max) * (h - 20);
                const x = pad + i * (bw + gap);
                return (
                    <g key={d.date}>
                        <title>{`${d.date}: ${money(d.revenue)} · ${d.orders} order(s)`}</title>
                        <rect x={x} y={h - bh} width={bw} height={Math.max(bh, 3)} rx="6" fill="url(#bar)" opacity={bh ? 1 : 0.25} />
                        <text x={x + bw / 2} y={h + 16} textAnchor="middle" fontSize="11" fill="#94a3b8">
                            {new Date(d.date).toLocaleDateString('en-ZA', { weekday: 'short' })}
                        </text>
                    </g>
                );
            })}
        </svg>
    );
}

export default function AdminDashboard() {
    const { user } = useAuth();
    const [s, setS] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {
        adminStats().then(setS).catch(() => setError('Could not load dashboard data. Is the backend running?'));
    }, []);

    if (error) return <p className="rounded-xl bg-red-50 p-4 text-sm text-red-700">{error}</p>;
    if (!s) return <LoadingSpinner label="Loading dashboard..." />;

    const total = Object.values(s.ordersByStatus).reduce((a, b) => a + b, 0) || 1;
    const alerts = [
        { show: s.ordersByStatus.PENDING > 0, text: `${s.ordersByStatus.PENDING} order(s) waiting to be confirmed`, to: '/admin/orders', color: 'amber' },
        { show: s.lowStockCount > 0, text: `${s.lowStockCount} product(s) low on stock`, to: '/admin/products', color: 'rose' },
        { show: s.openEnquiries > 0, text: `${s.openEnquiries} new customer enquiry(ies)`, to: '/admin/enquiries', color: 'violet' },
    ].filter((a) => a.show);
    const alertStyle = { amber: 'bg-amber-50 text-amber-800 border-amber-200', rose: 'bg-rose-50 text-rose-800 border-rose-200', violet: 'bg-violet-50 text-violet-800 border-violet-200' };

    return (
        <div className="animate-fade-in">
            <div className="relative mb-6 overflow-hidden rounded-3xl bg-gradient-to-br from-surface-950 via-brand-950 to-brand-700 p-6 text-white shadow-glow sm:p-8">
                <h1 className="mt-2 font-display text-2xl font-extrabold sm:text-3xl">Welcome back, {user?.firstName}</h1>
                <p className="mt-1 text-sm text-slate-300">Here is how TechVault is doing today.</p>
            </div>

            {alerts.length > 0 && (
                <div className="mb-6 flex flex-wrap gap-3">
                    {alerts.map((a) => (
                        <Link key={a.text} to={a.to} className={`flex items-center gap-2 rounded-xl border px-4 py-2 text-sm font-medium transition hover:shadow ${alertStyle[a.color]}`}>
                            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path d={ICONS.alert} /></svg>
                            {a.text} →
                        </Link>
                    ))}
                </div>
            )}

            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
                <StatCard label="Revenue (delivered)" value={money(s.revenue)} hint="From delivered orders" icon={ICONS.money} tone="green" />
                <StatCard label="In the pipeline" value={money(s.pipelineValue)} hint="Pending, confirmed and shipped" icon={ICONS.deliveries} tone="cyan" />
                <StatCard label="Orders" value={s.totalOrders} hint={`${s.ordersByStatus.PENDING} pending`} icon={ICONS.orders} tone="brand" />
                <StatCard label="Customers" value={s.customers} icon={ICONS.customers} tone="violet" />
                <StatCard label="Products" value={s.products} hint={`${s.lowStockCount} low on stock`} icon={ICONS.products} tone="amber" />
                <StatCard label="Open enquiries" value={s.openEnquiries} icon={ICONS.enquiries} tone="rose" />
            </div>

            <div className="mt-6 grid gap-6 lg:grid-cols-3">
                <section className="card p-5 lg:col-span-2">
                    <h2 className="font-display text-lg font-bold text-slate-900">Sales, last 7 days</h2>
                    <p className="mb-2 text-xs text-slate-400">Cancelled orders are not counted. Hover a bar for details.</p>
                    <SalesChart data={s.last7Days} />
                </section>

                <section className="card p-5">
                    <h2 className="font-display text-lg font-bold text-slate-900">Orders by status</h2>
                    <div className="mt-4 flex h-3 overflow-hidden rounded-full bg-slate-100">
                        {Object.entries(s.ordersByStatus).map(([k, v]) => (
                            <div key={k} style={{ width: `${(v / total) * 100}%`, background: STATUS_COLORS[k] }} title={`${k}: ${v}`} />
                        ))}
                    </div>
                    <ul className="mt-4 space-y-2 text-sm">
                        {Object.entries(s.ordersByStatus).map(([k, v]) => (
                            <li key={k} className="flex items-center justify-between">
                                <span className="flex items-center gap-2 text-slate-600">
                                    <span className="h-2.5 w-2.5 rounded-full" style={{ background: STATUS_COLORS[k] }} />
                                    {k.charAt(0) + k.slice(1).toLowerCase()}
                                </span>
                                <span className="font-semibold text-slate-900">{v}</span>
                            </li>
                        ))}
                    </ul>
                </section>
            </div>

            <div className="mt-6 grid gap-6 lg:grid-cols-3">
                <section className="card p-5 lg:col-span-2">
                    <div className="mb-3 flex items-center justify-between">
                        <h2 className="font-display text-lg font-bold text-slate-900">Recent orders</h2>
                        <Link to="/admin/orders" className="text-sm font-medium text-brand-600 hover:underline">View all →</Link>
                    </div>
                    {s.recentOrders.length === 0 ? (
                        <p className="py-6 text-center text-sm text-slate-400">No orders yet.</p>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-sm">
                                <tbody className="divide-y divide-slate-100">
                                {s.recentOrders.map((o) => (
                                    <tr key={o.id}>
                                        <td className="py-2.5 font-semibold text-slate-900">#{o.id}</td>
                                        <td className="py-2.5 text-slate-600">{o.customer}</td>
                                        <td className="py-2.5 text-slate-400">{fmtDate(o.date)}</td>
                                        <td className="py-2.5 font-medium">{money(o.total)}</td>
                                        <td className="py-2.5 text-right"><StatusBadge status={o.status} /></td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>

                <div className="space-y-6">
                    <section className="card p-5">
                        <h2 className="font-display text-lg font-bold text-slate-900">Top products</h2>
                        {s.topProducts.length === 0 ? (
                            <p className="mt-3 text-sm text-slate-400">No sales yet.</p>
                        ) : (
                            <ol className="mt-3 space-y-2 text-sm">
                                {s.topProducts.map((p, i) => (
                                    <li key={p.name} className="flex items-center justify-between gap-2">
                                        <span className="truncate text-slate-700"><span className="mr-2 font-mono text-xs text-slate-400">{i + 1}</span>{p.name}</span>
                                        <span className="shrink-0 font-semibold text-slate-900">{p.units} sold</span>
                                    </li>
                                ))}
                            </ol>
                        )}
                    </section>
                    <section className="card p-5">
                        <h2 className="font-display text-lg font-bold text-slate-900">Low stock</h2>
                        {s.lowStock.length === 0 ? (
                            <p className="mt-3 text-sm text-slate-400">All products are well stocked.</p>
                        ) : (
                            <ul className="mt-3 space-y-2 text-sm">
                                {s.lowStock.map((p) => (
                                    <li key={p.id} className="flex items-center justify-between gap-2">
                                        <span className="truncate text-slate-700">{p.name}</span>
                                        <span className={`shrink-0 rounded-full px-2 py-0.5 text-xs font-semibold ${p.stock === 0 ? 'bg-red-100 text-red-700' : 'bg-amber-100 text-amber-800'}`}>
                                            {p.stock === 0 ? 'Out of stock' : `${p.stock} left`}
                                        </span>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </section>
                </div>
            </div>
        </div>
    );
}
