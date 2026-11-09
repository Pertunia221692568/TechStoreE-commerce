import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from '../components/Logo';

export default function Login() {
    const [searchParams] = useSearchParams();
    const initialMode = searchParams.get('mode') === 'register' ? 'register' : 'login';

    const [mode, setMode] = useState(initialMode);
    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
        password: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const { login, register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    // ── LOGIN ──────────────────────────────────────────────────
    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            await login(form.email.trim(), form.password);
            navigate('/');
        } catch (err) {
            const msg =
                err.response?.data?.error ||
                err.response?.data?.message ||
                'Could not connect to the server!';
            setError(typeof msg === 'string' ? msg : 'Login failed');
        } finally {
            setLoading(false);
        }
    };

    // ── REGISTER ───────────────────────────────────────────────
    const handleRegister = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        if (!form.password || form.password.length < 6) {
            setError('Password must be at least 6 characters.');
            setLoading(false);
            return;
        }

        try {
            await register({
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                email: form.email.trim(),
                phone: form.phone || '',
                address: form.address || '',
                password: form.password,
            });
            navigate('/');
        } catch (err) {
            const msg =
                err.response?.data?.error ||
                err.response?.data?.message ||
                'Registration failed';
            setError(
                typeof msg === 'string'
                    ? msg
                    : 'Registration failed. Email may already exist.'
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="mx-auto flex min-h-[70vh] max-w-md flex-col justify-center px-4 py-12 sm:px-6">
            <div className="mb-8 flex flex-col items-center text-center">
                <Logo size="lg" to="/" />
                <h1 className="mt-6 font-display text-2xl font-bold text-slate-900">
                    {mode === 'login' ? 'Sign in to your account' : 'Create your account'}
                </h1>
                <p className="mt-2 text-sm text-slate-500">
                    {mode === 'login'
                        ? 'Use your registered email and password.'
                        : 'Join TechVault to track orders and manage your profile.'}
                </p>
            </div>

            {/* Toggle buttons */}
            <div className="mb-6 grid grid-cols-2 gap-2 rounded-xl bg-slate-100 p-1">
                <button
                    type="button"
                    onClick={() => {
                        setMode('login');
                        setError('');
                    }}
                    className={`rounded-lg py-2.5 text-sm font-semibold transition ${
                        mode === 'login'
                            ? 'bg-white text-brand-700 shadow-sm'
                            : 'text-slate-600 hover:text-slate-900'
                    }`}
                >
                    Sign in
                </button>
                <button
                    type="button"
                    onClick={() => {
                        setMode('register');
                        setError('');
                    }}
                    className={`rounded-lg py-2.5 text-sm font-semibold transition ${
                        mode === 'register'
                            ? 'bg-white text-brand-700 shadow-sm'
                            : 'text-slate-600 hover:text-slate-900'
                    }`}
                >
                    Register
                </button>
            </div>

            {/* Form card */}
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                <form
                    onSubmit={mode === 'login' ? handleLogin : handleRegister}
                    className="space-y-4"
                >
                    {mode === 'register' && (
                        <>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="mb-1 block text-sm font-medium text-slate-700">
                                        First name
                                    </label>
                                    <input
                                        type="text"
                                        name="firstName"
                                        value={form.firstName}
                                        onChange={handleChange}
                                        required
                                        className="input-field"
                                        placeholder="First name"
                                    />
                                </div>
                                <div>
                                    <label className="mb-1 block text-sm font-medium text-slate-700">
                                        Last name
                                    </label>
                                    <input
                                        type="text"
                                        name="lastName"
                                        value={form.lastName}
                                        onChange={handleChange}
                                        required
                                        className="input-field"
                                        placeholder="Last name"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="mb-1 block text-sm font-medium text-slate-700">
                                    Phone
                                </label>
                                <input
                                    type="tel"
                                    name="phone"
                                    value={form.phone}
                                    onChange={handleChange}
                                    className="input-field"
                                    placeholder="Phone number"
                                />
                            </div>

                            <div>
                                <label className="mb-1 block text-sm font-medium text-slate-700">
                                    Address
                                </label>
                                <input
                                    type="text"
                                    name="address"
                                    value={form.address}
                                    onChange={handleChange}
                                    className="input-field"
                                    placeholder="Your address"
                                />
                            </div>
                        </>
                    )}

                    <div>
                        <label className="mb-1 block text-sm font-medium text-slate-700">
                            Email
                        </label>
                        <input
                            type="email"
                            name="email"
                            value={form.email}
                            onChange={handleChange}
                            required
                            className="input-field"
                            placeholder="you@example.com"
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-sm font-medium text-slate-700">
                            Password
                        </label>
                        <input
                            type="password"
                            name="password"
                            value={form.password}
                            onChange={handleChange}
                            required
                            className="input-field"
                            placeholder={
                                mode === 'register' ? 'Min 6 characters' : 'Your password'
                            }
                            minLength={mode === 'register' ? 6 : 1}
                        />
                    </div>

                    {error && (
                        <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="btn-primary w-full !py-3"
                    >
                        {loading
                            ? 'Please wait...'
                            : mode === 'login'
                                ? 'Sign in'
                                : 'Create account'}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-slate-500">
                    {mode === 'login' ? (
                        <>
                            Don&apos;t have an account?{' '}
                            <button
                                type="button"
                                onClick={() => setMode('register')}
                                className="font-semibold text-brand-600 hover:text-brand-700"
                            >
                                Register
                            </button>
                        </>
                    ) : (
                        <>
                            Already have an account?{' '}
                            <button
                                type="button"
                                onClick={() => setMode('login')}
                                className="font-semibold text-brand-600 hover:text-brand-700"
                            >
                                Sign in
                            </button>
                        </>
                    )}
                </p>
            </div>

            <p className="mt-6 text-center text-sm text-slate-500">
                <Link to="/" className="font-medium text-brand-600 hover:text-brand-700">
                    ← Back to home
                </Link>
            </p>
        </div>
    );
}