import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { createEnquiry } from '../services/api';

export default function Contact() {
    const { user } = useAuth();
    const [form, setForm] = useState({
        name: user ? `${user.firstName || ''} ${user.lastName || ''}`.trim() : '',
        email: user?.email || '',
        orderNumber: '',
        subject: '',
        message: '',
    });
    const [status, setStatus] = useState({ type: '', text: '' });
    const [sending, setSending] = useState(false);
    const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

    const submit = async (e) => {
        e.preventDefault();
        setSending(true);
        setStatus({ type: '', text: '' });
        try {
            await createEnquiry(form);
            setStatus({ type: 'ok', text: 'Thank you! Your message has been sent. We will get back to you soon.' });
            setForm((f) => ({ ...f, orderNumber: '', subject: '', message: '' }));
        } catch (err) {
            setStatus({ type: 'error', text: err.response?.data?.error || 'Could not send your message. Please try again.' });
        } finally {
            setSending(false);
        }
    };

    return (
        <div className="mx-auto max-w-5xl px-4 py-12 sm:px-6 lg:px-8 animate-fade-in">
            <h1 className="font-display text-3xl font-bold text-slate-900">Contact Us</h1>
            <p className="mt-3 text-slate-600">We are happy to help with orders, products, and returns.</p>

            <div className="mt-8 grid gap-6 lg:grid-cols-5">
                <form onSubmit={submit} className="card space-y-3 p-6 lg:col-span-3">
                    <h2 className="font-display text-lg font-bold text-slate-900">Send us a message</h2>
                    <div className="grid gap-3 sm:grid-cols-2">
                        <input className="input-field" placeholder="Your name" value={form.name} onChange={set('name')} required maxLength={100} />
                        <input className="input-field" type="email" placeholder="Email address" value={form.email} onChange={set('email')} required maxLength={150} />
                    </div>
                    <input className="input-field" placeholder="Order number (optional)" value={form.orderNumber} onChange={set('orderNumber')} maxLength={30} />
                    <input className="input-field" placeholder="Subject" value={form.subject} onChange={set('subject')} required maxLength={150} />
                    <textarea className="input-field min-h-[140px]" placeholder="How can we help?" value={form.message} onChange={set('message')} required maxLength={2000} />
                    {status.text && (
                        <p className={`rounded-lg px-3 py-2 text-sm ${status.type === 'ok' ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-700'}`}>{status.text}</p>
                    )}
                    <button type="submit" disabled={sending} className="btn-primary">{sending ? 'Sending…' : 'Send message'}</button>
                </form>

                <div className="card space-y-4 p-6 lg:col-span-2">
                    <div><p className="text-xs font-semibold uppercase tracking-wide text-slate-400">Email</p><p className="mt-1 text-slate-800">support@techvault.co.za</p></div>
                    <div><p className="text-xs font-semibold uppercase tracking-wide text-slate-400">Phone</p><p className="mt-1 text-slate-800">+27 01 576 0849</p></div>
                    <div><p className="text-xs font-semibold uppercase tracking-wide text-slate-400">Hours</p><p className="mt-1 text-slate-800">Monday – Friday, 09:00 – 17:00 (SAST)</p></div>
                    <div><p className="text-xs font-semibold uppercase tracking-wide text-slate-400">Address</p><p className="mt-1 text-slate-800">Cape Town, South Africa</p></div>
                </div>
            </div>
            <Link to="/" className="mt-8 inline-block text-sm text-brand-600 hover:underline">← Back to home</Link>
        </div>
    );
}
