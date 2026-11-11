import { useEffect, useMemo, useState } from 'react';
import { adminPayments } from '../../services/api';
import LoadingSpinner from '../../components/LoadingSpinner';
import { PageHeader, StatCard, Empty, Th, Td, StatusBadge, ICONS, money, fmtDate } from '../../components/admin/ui';

export default function AdminPayments() {
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);
    const [q, setQ] = useState('');

    useEffect(() => { adminPayments().then(setRows).catch(() => setRows([])).finally(() => setLoading(false)); }, []);

    const shown = useMemo(() => rows.filter((p) => `${p.paymentId} ${p.orderId} ${p.cardHolder} ${p.method}`.toLowerCase().includes(q.toLowerCase())), [rows, q]);
    const total = rows.reduce((n, p) => n + Number(p.amount || 0), 0);

    if (loading) return <LoadingSpinner label="Loading payments..." />;

    return (
        <div className="animate-fade-in">
            <PageHeader title="Payments" subtitle="Payment records for placed orders. Full card numbers are never stored or shown." />
            <div className="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                <StatCard label="Payments recorded" value={rows.length} icon={ICONS.payments} tone="brand" />
                <StatCard label="Total value" value={money(total)} icon={ICONS.money} tone="green" />
                <StatCard label="Card payments" value={rows.filter((p) => p.method === 'Card').length} icon={ICONS.payments} tone="violet" />
            </div>
            <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search payment, order, holder..." className="input-field mb-4 max-w-sm" />
            {shown.length === 0 ? <Empty text="No payments recorded yet." /> : (
                <div className="card overflow-x-auto">
                    <table className="w-full text-sm">
                        <thead className="border-b border-slate-100 bg-slate-50"><tr>
                            <Th>Payment</Th><Th>Order</Th><Th>Method</Th><Th>Card holder</Th><Th>Amount</Th><Th>Status</Th><Th>Paid</Th>
                        </tr></thead>
                        <tbody className="divide-y divide-slate-100">
                        {shown.map((p) => (
                            <tr key={p.id} className="hover:bg-slate-50/60">
                                <Td className="font-mono text-xs">{p.paymentId}</Td>
                                <Td className="font-semibold text-slate-900">#{p.orderId}</Td>
                                <Td>{p.method === 'Card' && p.cardLast4 ? `Card •••• ${p.cardLast4}` : p.method}</Td>
                                <Td>{p.cardHolder || '—'}</Td>
                                <Td className="font-medium">{money(p.amount)}</Td>
                                <Td><StatusBadge status={String(p.status || '').toUpperCase()} /></Td>
                                <Td className="text-slate-500">{fmtDate(p.paidAt)}</Td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
