import { useEffect, useMemo, useState } from 'react';
import { adminCustomers, adminSetCustomerEnabled } from '../../services/api';
import LoadingSpinner from '../../components/LoadingSpinner';
import { PageHeader, Notice, useNotice, Empty, Th, Td, StatusBadge, money, fmtDate } from '../../components/admin/ui';

export default function AdminCustomers() {
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);
    const [q, setQ] = useState('');
    const [notice, show] = useNotice();

    const load = () => adminCustomers().then(setRows).catch(() => show('error', 'Could not load customers.')).finally(() => setLoading(false));
    useEffect(() => { load(); }, []);

    const shown = useMemo(() => rows.filter((c) => `${c.name} ${c.email} ${c.phone}`.toLowerCase().includes(q.toLowerCase())), [rows, q]);

    const toggle = async (c) => {
        const next = !c.enabled;
        if (!window.confirm(`${next ? 'Enable' : 'Disable'} the account of ${c.name}?${next ? '' : ' They will no longer be able to sign in.'}`)) return;
        try {
            await adminSetCustomerEnabled(c.userId, next);
            show('ok', `Account ${next ? 'enabled' : 'disabled'}.`);
            load();
        } catch (err) {
            show('error', err.response?.data?.error || 'Could not update the account.');
        }
    };

    if (loading) return <LoadingSpinner label="Loading customers..." />;

    return (
        <div className="animate-fade-in">
            <PageHeader title="Customers" subtitle={`${rows.length} registered customers`} />
            <Notice notice={notice} />
            <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search name, email or phone..." className="input-field mb-4 max-w-sm" />
            {shown.length === 0 ? <Empty text="No customers found." /> : (
                <div className="card overflow-x-auto">
                    <table className="w-full text-sm">
                        <thead className="border-b border-slate-100 bg-slate-50"><tr>
                            <Th>Customer</Th><Th>Phone</Th><Th>Joined</Th><Th>Last login</Th><Th>Orders</Th><Th>Total spent</Th><Th>Account</Th><Th className="text-right">Actions</Th>
                        </tr></thead>
                        <tbody className="divide-y divide-slate-100">
                        {shown.map((c) => (
                            <tr key={c.userId} className="hover:bg-slate-50/60">
                                <Td><p className="font-semibold text-slate-900">{c.name}</p><p className="text-xs text-slate-400">{c.email}</p></Td>
                                <Td>{c.phone || '—'}</Td>
                                <Td className="text-slate-500">{fmtDate(c.createdAt, false)}</Td>
                                <Td className="text-slate-500">{fmtDate(c.lastLoginAt)}</Td>
                                <Td>{c.orders}</Td>
                                <Td className="font-medium">{money(c.totalSpent)}</Td>
                                <Td><StatusBadge status={c.enabled ? 'ACTIVE' : 'DISABLED'} /></Td>
                                <Td className="text-right">
                                    <button onClick={() => toggle(c)} className={`rounded-lg border px-3 py-1 text-xs font-semibold ${c.enabled ? 'border-red-200 text-red-600 hover:bg-red-50' : 'border-emerald-200 text-emerald-700 hover:bg-emerald-50'}`}>
                                        {c.enabled ? 'Disable' : 'Enable'}
                                    </button>
                                </Td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
