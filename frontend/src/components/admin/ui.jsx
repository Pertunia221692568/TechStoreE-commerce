import { useState } from 'react';

export const money = (v) =>
    new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(v || 0));

export const fmtDate = (iso, time = true) =>
    iso
        ? new Date(iso).toLocaleString('en-ZA', time ? { dateStyle: 'medium', timeStyle: 'short' } : { dateStyle: 'medium' })
        : '—';

export const STATUS_STYLES = {
    PENDING: 'bg-amber-100 text-amber-800',
    CONFIRMED: 'bg-sky-100 text-sky-800',
    SHIPPED: 'bg-violet-100 text-violet-800',
    DISPATCHED: 'bg-violet-100 text-violet-800',
    DELIVERED: 'bg-emerald-100 text-emerald-800',
    CANCELLED: 'bg-red-100 text-red-800',
    NEW: 'bg-rose-100 text-rose-800',
    IN_PROGRESS: 'bg-amber-100 text-amber-800',
    RESOLVED: 'bg-emerald-100 text-emerald-800',
    PAID: 'bg-emerald-100 text-emerald-800',
    SUCCESS: 'bg-emerald-100 text-emerald-800',
    ACTIVE: 'bg-emerald-100 text-emerald-800',
    DISABLED: 'bg-slate-200 text-slate-600',
};

export function StatusBadge({ status }) {
    return (
        <span className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-semibold ${STATUS_STYLES[status] || 'bg-slate-100 text-slate-700'}`}>
            {String(status || '—').replace('_', ' ')}
        </span>
    );
}

export function PageHeader({ title, subtitle, action }) {
    return (
        <div className="mb-6 flex flex-wrap items-end justify-between gap-3">
            <div>
                <h1 className="font-display text-2xl font-bold text-slate-900">{title}</h1>
                {subtitle && <p className="mt-1 text-sm text-slate-500">{subtitle}</p>}
            </div>
            {action}
        </div>
    );
}

export function StatCard({ label, value, hint, icon, tone = 'brand' }) {
    const tones = {
        brand: 'from-brand-500 to-brand-700',
        green: 'from-emerald-500 to-emerald-700',
        amber: 'from-amber-500 to-orange-600',
        violet: 'from-violet-500 to-violet-700',
        rose: 'from-rose-500 to-rose-700',
        cyan: 'from-cyan-500 to-sky-600',
    };
    return (
        <div className="card flex items-center gap-4 p-5 transition hover:-translate-y-0.5 hover:shadow-md">
            <span className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br text-white shadow ${tones[tone]}`}>
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8} strokeLinecap="round" strokeLinejoin="round">
                    <path d={icon} />
                </svg>
            </span>
            <div className="min-w-0">
                <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">{label}</p>
                <p className="truncate font-display text-2xl font-bold text-slate-900">{value}</p>
                {hint && <p className="text-xs text-slate-500">{hint}</p>}
            </div>
        </div>
    );
}

export function Modal({ title, onClose, children, wide }) {
    return (
        <div className="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-slate-900/60 p-4 backdrop-blur-sm" onClick={onClose}>
            <div
                className={`my-8 w-full ${wide ? 'max-w-3xl' : 'max-w-lg'} rounded-2xl bg-white p-6 shadow-2xl animate-scale-in`}
                onClick={(e) => e.stopPropagation()}
            >
                <div className="mb-4 flex items-center justify-between">
                    <h2 className="font-display text-lg font-bold text-slate-900">{title}</h2>
                    <button onClick={onClose} className="rounded-lg px-2 py-1 text-slate-400 hover:bg-slate-100 hover:text-slate-700" aria-label="Close">✕</button>
                </div>
                {children}
            </div>
        </div>
    );
}

export function useNotice() {
    const [notice, setNotice] = useState(null);
    const show = (type, text) => {
        setNotice({ type, text });
        setTimeout(() => setNotice(null), 4000);
    };
    return [notice, show];
}

export function Notice({ notice }) {
    if (!notice) return null;
    return (
        <div className={`mb-4 rounded-xl border px-4 py-3 text-sm font-medium ${
            notice.type === 'ok' ? 'border-emerald-200 bg-emerald-50 text-emerald-800' : 'border-red-200 bg-red-50 text-red-800'
        }`}>
            {notice.text}
        </div>
    );
}

export function Empty({ text }) {
    return <div className="rounded-2xl border border-dashed border-slate-200 py-14 text-center text-sm text-slate-400">{text}</div>;
}

export const Th = ({ children, className = '' }) => (
    <th className={`whitespace-nowrap px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-400 ${className}`}>{children}</th>
);
export const Td = ({ children, className = '' }) => <td className={`px-4 py-3 align-middle ${className}`}>{children}</td>;

export const ICONS = {
    dashboard: 'M4 4h6v6H4zM14 4h6v6h-6zM4 14h6v6H4zM14 14h6v6h-6z',
    products: 'M21 8l-9-5-9 5v8l9 5 9-5V8zM3 8l9 5 9-5M12 13v8',
    orders: 'M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01',
    customers: 'M17 20v-2a4 4 0 00-4-4H7a4 4 0 00-4 4v2M10 10a4 4 0 100-8 4 4 0 000 8zM21 20v-2a4 4 0 00-3-3.87M16 2.13a4 4 0 010 7.75',
    deliveries: 'M1 3h15v13H1zM16 8h4l3 3v5h-7zM5.5 21a2.5 2.5 0 100-5 2.5 2.5 0 000 5zM18.5 21a2.5 2.5 0 100-5 2.5 2.5 0 000 5z',
    payments: 'M2 5h20v14H2zM2 10h20',
    enquiries: 'M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2zM22 6l-10 7L2 6',
    money: 'M12 2v20M17 6H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6',
    alert: 'M10.3 3.9L1.8 18a2 2 0 001.7 3h17a2 2 0 001.7-3L13.7 3.9a2 2 0 00-3.4 0zM12 9v4M12 17h.01',
};
