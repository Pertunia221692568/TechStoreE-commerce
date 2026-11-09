import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function AdminLogin() {
    const { login, logout, isAuthenticated, isAdmin } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [form, setForm] = useState({ email: '', password: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    if (isAuthenticated && isAdmin) return <Navigate to="/admin" replace />;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const data = await login(form.email.trim(), form.password);
            if (data.role !== 'ADMIN') {
                logout();
                setError('This account is not an admin account.');
                return;
            }
            navigate(location.state?.from || '/admin', { replace: true });
        } catch (err) {
            setError(err.response?.data?.error || 'Could not sign in. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-slate-900 px-4">
            <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-xl">
                <p className="text-xs font-semibold uppercase tracking-wide text-brand-600">Staff only</p>
                <h1 className="mt-1 font-display text-2xl font-bold text-slate-900">Admin sign in</h1>
                <p className="mt-2 text-sm text-slate-500">Sign in with your admin email and password.</p>
                <form onSubmit={handleSubmit} className="mt-6 space-y-3">
                    <input
                        type="email"
                        value={form.email}
                        onChange={(e) => setForm({ ...form, email: e.target.value })}
                        placeholder="Admin email"
                        className="input-field"
                        autoComplete="username"
                        required
                    />
                    <input
                        type="password"
                        value={form.password}
                        onChange={(e) => setForm({ ...form, password: e.target.value })}
                        placeholder="Password"
                        className="input-field"
                        autoComplete="current-password"
                        required
                    />
                    {error && <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}
                    <button type="submit" disabled={loading} className="btn-primary w-full">
                        {loading ? 'Signing in…' : 'Sign in as admin'}
                    </button>
                </form>
                <Link to="/" className="mt-4 block text-center text-sm text-brand-600 hover:underline">
                    ← Back to store
                </Link>
            </div>
        </div>
    );
}
