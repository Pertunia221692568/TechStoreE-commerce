import { useEffect, useState } from 'react';
import { adminEnquiries, adminUpdateEnquiry } from '../../services/api';
import LoadingSpinner from '../../components/LoadingSpinner';
import { PageHeader, Modal, Notice, useNotice, Empty, StatusBadge, fmtDate } from '../../components/admin/ui';

const FILTERS = ['ALL', 'NEW', 'IN_PROGRESS', 'RESOLVED'];

export default function AdminEnquiries() {
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState('ALL');
    const [open, setOpen] = useState(null);
    const [reply, setReply] = useState('');
    const [notice, show] = useNotice();

    const load = () => adminEnquiries().then(setRows).catch(() => show('error', 'Could not load enquiries.')).finally(() => setLoading(false));
    useEffect(() => { load(); }, []);

    const openIt = (e) => { setOpen(e); setReply(e.adminReply || ''); };
    const save = async (status) => {
        try {
            const body = { adminReply: reply };
            if (status) body.status = status;
            await adminUpdateEnquiry(open.id, body);
            show('ok', 'Enquiry updated.');
            setOpen(null);
            load();
        } catch (err) {
            show('error', err.response?.data?.error || 'Could not update.');
        }
    };

    if (loading) return <LoadingSpinner label="Loading enquiries..." />;
    const shown = rows.filter((r) => filter === 'ALL' || r.status === filter);
    const count = (s) => (s === 'ALL' ? rows.length : rows.filter((r) => r.status === s).length);

    return (
        <div className="animate-fade-in">
            <PageHeader title="Customer enquiries" subtitle="Messages sent from the Contact page." />
            <Notice notice={notice} />
            <div className="mb-4 flex flex-wrap gap-2">
                {FILTERS.map((s) => (
                    <button key={s} onClick={() => setFilter(s)}
                            className={`rounded-full px-3.5 py-1.5 text-xs font-semibold transition ${filter === s ? 'bg-brand-600 text-white' : 'border border-slate-200 bg-white text-slate-600 hover:border-brand-300'}`}>
                        {s === 'ALL' ? 'All' : s.replace('_', ' ').toLowerCase().replace(/^./, (c) => c.toUpperCase())} ({count(s)})
                    </button>
                ))}
            </div>
            {shown.length === 0 ? <Empty text="No enquiries here." /> : (
                <div className="space-y-3">
                    {shown.map((e) => (
                        <button key={e.id} onClick={() => openIt(e)} className="card block w-full p-4 text-left transition hover:-translate-y-0.5 hover:shadow-md">
                            <div className="flex flex-wrap items-center justify-between gap-2">
                                <p className="font-semibold text-slate-900">{e.subject}</p>
                                <StatusBadge status={e.status} />
                            </div>
                            <p className="mt-1 line-clamp-2 text-sm text-slate-600">{e.message}</p>
                            <p className="mt-2 text-xs text-slate-400">{e.name} · {e.email}{e.orderNumber ? ` · Order ${e.orderNumber}` : ''} · {fmtDate(e.createdAt)}</p>
                        </button>
                    ))}
                </div>
            )}
            {open && (
                <Modal title={open.subject} onClose={() => setOpen(null)} wide>
                    <p className="text-xs text-slate-400">{open.name} · <a className="text-brand-600 hover:underline" href={`mailto:${open.email}?subject=Re: ${encodeURIComponent(open.subject)}`}>{open.email}</a>{open.orderNumber ? ` · Order ${open.orderNumber}` : ''} · {fmtDate(open.createdAt)}</p>
                    <p className="mt-3 whitespace-pre-wrap rounded-xl bg-slate-50 p-4 text-sm text-slate-800">{open.message}</p>
                    <label className="mt-4 block text-sm font-medium text-slate-700">Your reply / internal note
                        <textarea value={reply} onChange={(e) => setReply(e.target.value)} maxLength={2000} className="input-field mt-1 min-h-[110px]" placeholder="Write the reply you sent or a note for the team..." />
                    </label>
                    <div className="mt-4 flex flex-wrap justify-end gap-2">
                        <button onClick={() => save('IN_PROGRESS')} className="btn-secondary">Mark in progress</button>
                        <button onClick={() => save('RESOLVED')} className="btn-primary">Save &amp; resolve</button>
                    </div>
                </Modal>
            )}
        </div>
    );
}
