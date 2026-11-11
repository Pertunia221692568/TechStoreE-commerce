import { useEffect, useMemo, useState } from 'react';
import { adminDeliveries, adminUpdateDelivery } from '../../services/api';
import LoadingSpinner from '../../components/LoadingSpinner';
import { PageHeader, Modal, Notice, useNotice, Empty, Th, Td, StatusBadge, fmtDate } from '../../components/admin/ui';

const COURIERS = ['The Courier Guy', 'Aramex', 'RAM Couriers'];

export default function AdminDeliveries() {
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);
    const [q, setQ] = useState('');
    const [edit, setEdit] = useState(null);
    const [notice, show] = useNotice();

    const load = () => adminDeliveries().then(setRows).catch(() => show('error', 'Could not load deliveries.')).finally(() => setLoading(false));
    useEffect(() => { load(); }, []);

    const shown = useMemo(() => rows.filter((d) => `${d.orderId} ${d.customer} ${d.carrier} ${d.trackingNumber}`.toLowerCase().includes(q.toLowerCase())), [rows, q]);

    const save = async (e) => {
        e.preventDefault();
        try {
            await adminUpdateDelivery(edit.id, { carrier: edit.carrier, trackingNumber: edit.trackingNumber });
            show('ok', 'Courier details updated.');
            setEdit(null);
            load();
        } catch (err) {
            show('error', err.response?.data?.error || 'Could not update.');
        }
    };

    if (loading) return <LoadingSpinner label="Loading deliveries..." />;

    return (
        <div className="animate-fade-in">
            <PageHeader title="Deliveries" subtitle="Couriers and tracking numbers. Delivery status follows the order status." />
            <Notice notice={notice} />
            <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search order, customer, courier..." className="input-field mb-4 max-w-sm" />
            {shown.length === 0 ? <Empty text="No deliveries yet." /> : (
                <div className="card overflow-x-auto">
                    <table className="w-full text-sm">
                        <thead className="border-b border-slate-100 bg-slate-50"><tr>
                            <Th>Order</Th><Th>Customer</Th><Th>Courier</Th><Th>Tracking no.</Th><Th>Status</Th><Th>ETA / delivered</Th><Th className="text-right">Actions</Th>
                        </tr></thead>
                        <tbody className="divide-y divide-slate-100">
                        {shown.map((d) => (
                            <tr key={d.id} className="hover:bg-slate-50/60">
                                <Td className="font-semibold text-slate-900">#{d.orderId}</Td>
                                <Td><p className="text-slate-800">{d.customer}</p><p className="max-w-[16rem] truncate text-xs text-slate-400">{d.address}</p></Td>
                                <Td className="font-medium">{d.carrier}</Td>
                                <Td className="font-mono text-xs">{d.trackingNumber}</Td>
                                <Td><StatusBadge status={d.status} /></Td>
                                <Td className="text-slate-500">{fmtDate(d.actualDeliveryDate || d.estimatedDeliveryDate, false)}</Td>
                                <Td className="text-right"><button onClick={() => setEdit({ ...d })} className="rounded-lg border border-slate-200 px-3 py-1 text-xs font-semibold text-slate-700 hover:bg-slate-50">Assign courier</button></Td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
            {edit && (
                <Modal title={`Courier for order #${edit.orderId}`} onClose={() => setEdit(null)}>
                    <form onSubmit={save} className="space-y-3">
                        <label className="block text-sm font-medium text-slate-700">Courier
                            <input list="couriers" className="input-field mt-1" value={edit.carrier} onChange={(e) => setEdit({ ...edit, carrier: e.target.value })} required maxLength={60} />
                            <datalist id="couriers">{COURIERS.map((c) => <option key={c} value={c} />)}</datalist>
                        </label>
                        <label className="block text-sm font-medium text-slate-700">Tracking number
                            <input className="input-field mt-1 font-mono" value={edit.trackingNumber} onChange={(e) => setEdit({ ...edit, trackingNumber: e.target.value })} required maxLength={60} />
                        </label>
                        <div className="flex justify-end gap-2 pt-2">
                            <button type="button" onClick={() => setEdit(null)} className="btn-secondary">Cancel</button>
                            <button type="submit" className="btn-primary">Save</button>
                        </div>
                    </form>
                </Modal>
            )}
        </div>
    );
}
