import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
    getCustomer,
    updateCustomer,
    deleteCustomer,
} from '../services/api';

export default function Profile() {
    const { user, logout, isAuthenticated } = useAuth();
    const navigate = useNavigate();

    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        if (!isAuthenticated || !user?.customerId) {
            navigate('/login');
            return;
        }

        getCustomer(user.customerId)
            .then((c) => {
                setForm({
                    firstName: c.firstName || '',
                    lastName: c.lastName || '',
                    email: c.email || '',
                    phone: c.phone || '',
                    address: c.address || '',
                });
            })
            .catch(() => {
                // Fallback to whatever is already in auth context
                setForm({
                    firstName: user.firstName || '',
                    lastName: user.lastName || '',
                    email: user.email || '',
                    phone: user.phone || '',
                    address: user.address || '',
                });
            })
            .finally(() => setLoading(false));
    }, [user, isAuthenticated, navigate]);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSave = async (e) => {
        e.preventDefault();
        setSaving(true);
        setMessage('');
        setError('');
        try {
            await updateCustomer(user.customerId, form);
            setMessage('Profile updated successfully.');
        } catch (err) {
            setError(err.response?.data?.error || err.message || 'Update failed');
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('Delete your profile permanently? This cannot be undone.')) {
            return;
        }
        try {
            await deleteCustomer(user.customerId);
            logout();
            navigate('/');
        } catch {
            setError('Could not delete profile. Please try again.');
        }
    };

    if (loading) {
        return (
            <div className="mx-auto max-w-lg px-4 py-20 text-center text-slate-500">
                Loading profile…
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-lg px-4 py-10 sm:px-6 animate-fade-in">
            <h1 className="font-display text-3xl font-bold text-slate-900">My profile</h1>
            <p className="mt-1 text-sm text-slate-500">
                Update your details and default shipping address.
            </p>

            <form onSubmit={handleSave} className="card mt-8 space-y-4 p-6">
                <div className="grid gap-4 sm:grid-cols-2">
                    <div>
                        <label className="mb-1 block text-xs font-medium text-slate-600">
                            First name
                        </label>
                        <input
                            name="firstName"
                            value={form.firstName}
                            onChange={handleChange}
                            className="input-field"
                            required
                        />
                    </div>
                    <div>
                        <label className="mb-1 block text-xs font-medium text-slate-600">
                            Last name
                        </label>
                        <input
                            name="lastName"
                            value={form.lastName}
                            onChange={handleChange}
                            className="input-field"
                            required
                        />
                    </div>
                </div>

                <div>
                    <label className="mb-1 block text-xs font-medium text-slate-600">Email</label>
                    <input
                        name="email"
                        type="email"
                        value={form.email}
                        onChange={handleChange}
                        className="input-field"
                        required
                    />
                </div>

                <div>
                    <label className="mb-1 block text-xs font-medium text-slate-600">Phone</label>
                    <input
                        name="phone"
                        value={form.phone}
                        onChange={handleChange}
                        className="input-field"
                        placeholder="Optional"
                    />
                </div>

                <div>
                    <label className="mb-1 block text-xs font-medium text-slate-600">
                        Default shipping address
                    </label>
                    <textarea
                        name="address"
                        value={form.address}
                        onChange={handleChange}
                        rows={3}
                        className="input-field"
                        placeholder="Street, suburb, city…"
                    />
                </div>

                {message && <p className="text-sm text-emerald-600">{message}</p>}
                {error && <p className="text-sm text-red-600">{error}</p>}

                <button type="submit" disabled={saving} className="btn-primary w-full">
                    {saving ? 'Saving…' : 'Save changes'}
                </button>
            </form>

            <div className="mt-8 rounded-2xl border border-red-100 bg-red-50 p-6">
                <h2 className="text-sm font-semibold text-red-800">Danger zone</h2>
                <p className="mt-1 text-sm text-red-600">
                    Permanently delete your customer profile.
                </p>
                <button
                    type="button"
                    onClick={handleDelete}
                    className="mt-4 rounded-xl border border-red-300 bg-white px-4 py-2 text-sm font-semibold text-red-700 hover:bg-red-50"
                >
                    Delete my profile
                </button>
            </div>

            <div className="mt-6 flex flex-wrap justify-center gap-4 text-sm">
                <Link to="/orders" className="font-medium text-brand-600 hover:text-brand-700">
                    My orders
                </Link>
                <button
                    type="button"
                    onClick={() => {
                        logout();
                        navigate('/');
                    }}
                    className="font-medium text-red-600 hover:text-red-700"
                >
                    Log out
                </button>
                <Link to="/" className="font-medium text-slate-500 hover:text-slate-700">
                    ← Home
                </Link>
            </div>
        </div>
    );
}