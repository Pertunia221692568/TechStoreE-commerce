import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from '../components/Logo';

export default function Login() {
    const [searchParams] = useSearchParams();
    const initialMode = searchParams.get('mode') === 'register' ? 'register' : 'login';

    // modes: login | register | verify | forgot | reset
    const [mode, setMode] = useState(initialMode);

    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
        password: '',
        code: '',
        newPassword: '',
    });

    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    // shown after register so the user knows the demo code
    const [demoCode, setDemoCode] = useState('');

    const { login, register, verifyEmail, forgotPassword, resetPassword } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const switchMode = (newMode) => {
        setMode(newMode);
        setError('');
        setSuccess('');
        setDemoCode('');
    };

    // ── LOGIN ──────────────────────────────────────────────────
    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const data = await login(form.email.trim(), form.password);
            navigate(data.role === 'ADMIN' ? '/admin' : '/');
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
        setSuccess('');

        try {
            const data = await register({
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                email: form.email.trim(),
                phone: form.phone || '',
                address: form.address || '',
                password: form.password,
            });

            // Backend returns verificationCode for demo purposes
            setDemoCode(data.verificationCode || '');
            setSuccess(
                data.message ||
                'Registration successful. Please enter the verification code.'
            );
            // Switch to verification screen
            setMode('verify');
        } catch (err) {
            const msg =
                err.response?.data?.error ||
                err.response?.data?.message ||
                'Registration failed';
            setError(typeof msg === 'string' ? msg : 'Registration failed. Email may already exist.');
        } finally {
            setLoading(false);
        }
    };

    // ── VERIFY EMAIL ───────────────────────────────────────────
    const handleVerify = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const data = await verifyEmail(form.code.trim());
            navigate(data.role === 'ADMIN' ? '/admin' : '/');
        } catch (err) {
            const msg =
                err.response?.data?.error ||
                err.response?.data?.message ||
                'Verification failed';
            setError(typeof msg === 'string' ? msg : 'Invalid or expired code');
        } finally {
            setLoading(false);
        }
    };

    // ── FORGOT PASSWORD ────────────────────────────────────────
    const handleForgot = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            const data = await forgotPassword(form.email.trim());
            setSuccess(data.message || 'If that email exists, a reset code has been sent.');
            // For demo we also receive the code
            if (data.resetCode) {
                setDemoCode(data.resetCode);
            }
            setMode('reset');
        } catch (err) {
            const msg =
                err.response?.data?.error ||
                err.response?.data?.message ||
                'Request failed';
            setError(typeof msg === 'string' ? msg : 'Could not process request');
        } finally {
            setLoading(false);
        }
    };

    // ── RESET PASSWORD ─────────────────────────────────────────
    const handleReset = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const data = await resetPassword(form.code.trim(), form.newPassword);
            setSuccess(data.message || 'Password reset successful. You can now sign in.');
            setMode('login');
            setDemoCode('');
            setForm((f) => ({ ...f, code: '', newPassword: '', password: '' }));
        } catch (err) {
            const msg =
                err.response?.data?.error ||
                err.response?.data?.message ||
                'Reset failed';
            setError(typeof msg === 'string' ? msg : 'Invalid code or weak password');
        } finally {
            setLoading(false);
        }
    };

    // ── Titles / subtitles per mode ────────────────────────────
    const titles = {
        login: 'Sign in to your account',
        register: 'Create your account',
        verify: 'Verify your email',
        forgot: 'Forgot your password?',
        reset: 'Set a new password',
    };

    const subtitles = {
        login: 'Use your registered email and password.',
        register: 'Join TechVault to track orders and manage your profile.',
        verify: 'Enter the 6-digit code that was generated for your account.',
        forgot: 'Enter your email and we will generate a reset code.',
        reset: 'Enter the reset code and your new password.',
    };

    return (
        <div className="mx-auto flex min-h-[70vh] max-w-md flex-col justify-center px-4 py-12 sm:px-6">
            <div className="mb-8 flex flex-col items-center text-center">
                <Logo size="lg" to="/" />
                <h1 className="mt-6 font-display text-2xl font-bold text-slate-900">
                    {titles[mode]}
                </h1>
                <p className="mt-2 text-sm text-slate-500">{subtitles[mode]}</p>
            </div>

            {/* Toggle only for login / register */}
            {(mode === 'login' || mode === 'register') && (
                <div className="mb-6 grid grid-cols-2 gap-2 rounded-xl bg-slate-100 p-1">
                    <button
                        type="button"
                        onClick={() => switchMode('login')}
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
                        onClick={() => switchMode('register')}
                        className={`rounded-lg py-2.5 text-sm font-semibold transition ${
                            mode === 'register'
                                ? 'bg-white text-brand-700 shadow-sm'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        Register
                    </button>
                </div>
            )}

            {/* Form card */}
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                {/* Success message */}
                {success && (
                    <div className="mb-4 rounded-lg bg-green-50 px-4 py-3 text-sm text-green-700">
                        {success}
                    </div>
                )}

                {/* Demo code helper (only for development) */}
                {demoCode && (
                    <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
                        <strong>Demo code:</strong> <span className="font-mono text-lg tracking-widest">{demoCode}</span>
                        <p className="mt-1 text-xs opacity-80">
                            In production this code would only be sent by email.
                        </p>
                    </div>
                )}

                {/* LOGIN FORM */}
                {mode === 'login' && (
                    <form onSubmit={handleLogin} className="space-y-4">
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Email</label>
                            <input
                                type="email"
                                name="email"
                                value={form.email}
                                onChange={handleChange}
                                required
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Password</label>
                            <input
                                type="password"
                                name="password"
                                value={form.password}
                                onChange={handleChange}
                                required
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>

                        {error && (
                            <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-xl bg-brand-600 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-700 disabled:opacity-60"
                        >
                            {loading ? 'Signing in…' : 'Sign in'}
                        </button>

                        <p className="text-center text-sm text-slate-500">
                            <button
                                type="button"
                                onClick={() => switchMode('forgot')}
                                className="font-medium text-brand-600 hover:underline"
                            >
                                Forgot password?
                            </button>
                        </p>
                    </form>
                )}

                {/* REGISTER FORM */}
                {mode === 'register' && (
                    <form onSubmit={handleRegister} className="space-y-4">
                        <div className="grid grid-cols-2 gap-3">
                            <div>
                                <label className="mb-1 block text-sm font-medium text-slate-700">First name</label>
                                <input
                                    type="text"
                                    name="firstName"
                                    value={form.firstName}
                                    onChange={handleChange}
                                    required
                                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                                />
                            </div>
                            <div>
                                <label className="mb-1 block text-sm font-medium text-slate-700">Last name</label>
                                <input
                                    type="text"
                                    name="lastName"
                                    value={form.lastName}
                                    onChange={handleChange}
                                    required
                                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                                />
                            </div>
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Email</label>
                            <input
                                type="email"
                                name="email"
                                value={form.email}
                                onChange={handleChange}
                                required
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Phone (optional)</label>
                            <input
                                type="text"
                                name="phone"
                                value={form.phone}
                                onChange={handleChange}
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Address (optional)</label>
                            <input
                                type="text"
                                name="address"
                                value={form.address}
                                onChange={handleChange}
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Password</label>
                            <input
                                type="password"
                                name="password"
                                value={form.password}
                                onChange={handleChange}
                                required
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                            <p className="mt-1 text-xs text-slate-400">
                                Min 8 characters, upper + lower + digit + special character
                            </p>
                        </div>

                        {error && (
                            <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-xl bg-brand-600 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-700 disabled:opacity-60"
                        >
                            {loading ? 'Creating account…' : 'Create account'}
                        </button>
                    </form>
                )}

                {/* VERIFY EMAIL FORM */}
                {mode === 'verify' && (
                    <form onSubmit={handleVerify} className="space-y-4">
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">
                                6-digit verification code
                            </label>
                            <input
                                type="text"
                                name="code"
                                value={form.code}
                                onChange={handleChange}
                                required
                                maxLength={6}
                                placeholder="123456"
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-center font-mono text-lg tracking-widest outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>

                        {error && (
                            <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-xl bg-brand-600 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-700 disabled:opacity-60"
                        >
                            {loading ? 'Verifying…' : 'Verify email'}
                        </button>

                        <p className="text-center text-sm text-slate-500">
                            <button
                                type="button"
                                onClick={() => switchMode('login')}
                                className="font-medium text-brand-600 hover:underline"
                            >
                                Back to Sign in
                            </button>
                        </p>
                    </form>
                )}

                {/* FORGOT PASSWORD FORM */}
                {mode === 'forgot' && (
                    <form onSubmit={handleForgot} className="space-y-4">
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Email</label>
                            <input
                                type="email"
                                name="email"
                                value={form.email}
                                onChange={handleChange}
                                required
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>

                        {error && (
                            <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-xl bg-brand-600 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-700 disabled:opacity-60"
                        >
                            {loading ? 'Sending…' : 'Send reset code'}
                        </button>

                        <p className="text-center text-sm text-slate-500">
                            <button
                                type="button"
                                onClick={() => switchMode('login')}
                                className="font-medium text-brand-600 hover:underline"
                            >
                                Back to Sign in
                            </button>
                        </p>
                    </form>
                )}

                {/* RESET PASSWORD FORM */}
                {mode === 'reset' && (
                    <form onSubmit={handleReset} className="space-y-4">
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">
                                6-digit reset code
                            </label>
                            <input
                                type="text"
                                name="code"
                                value={form.code}
                                onChange={handleChange}
                                required
                                maxLength={6}
                                placeholder="123456"
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-center font-mono text-lg tracking-widest outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">New password</label>
                            <input
                                type="password"
                                name="newPassword"
                                value={form.newPassword}
                                onChange={handleChange}
                                required
                                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100"
                            />
                            <p className="mt-1 text-xs text-slate-400">
                                Min 8 characters, upper + lower + digit + special character
                            </p>
                        </div>

                        {error && (
                            <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-xl bg-brand-600 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-700 disabled:opacity-60"
                        >
                            {loading ? 'Resetting…' : 'Reset password'}
                        </button>

                        <p className="text-center text-sm text-slate-500">
                            <button
                                type="button"
                                onClick={() => switchMode('login')}
                                className="font-medium text-brand-600 hover:underline"
                            >
                                Back to Sign in
                            </button>
                        </p>
                    </form>
                )}
            </div>

            {(mode === 'login' || mode === 'register') && (
                <p className="mt-6 text-center text-sm text-slate-500">
                    {mode === 'login' ? (
                        <>
                            Don&apos;t have an account?{' '}
                            <button
                                type="button"
                                onClick={() => switchMode('register')}
                                className="font-medium text-brand-600 hover:underline"
                            >
                                Register
                            </button>
                        </>
                    ) : (
                        <>
                            Already have an account?{' '}
                            <button
                                type="button"
                                onClick={() => switchMode('login')}
                                className="font-medium text-brand-600 hover:underline"
                            >
                                Sign in
                            </button>
                        </>
                    )}
                </p>
            )}
        </div>
    );
}
