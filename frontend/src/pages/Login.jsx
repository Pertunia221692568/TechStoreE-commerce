import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import {
    registerCustomer,
    createRegistration,
    createLogin,
} from '../services/api';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import Logo from '../components/Logo';

function nowLocalDateTime() {
    const now = new Date();
    const pad = (n) => String(n).padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
}

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
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleRegister = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        const email = form.email.trim().toLowerCase();
        const password = form.password;
        const when = nowLocalDateTime();

        if (!password || password.length < 4) {
            setError('Password must be at least 4 characters.');
            setLoading(false);
            return;
        }

        try {
            // 1) Customer profile
            const customer = await registerCustomer({
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                email,
                phone: form.phone || '',
                address: form.address || '',
            });

            // 2) registrations table
            try {
                await createRegistration({
                    email,
                    password,
                    registrationDate: when,
                });
            } catch (regErr) {
                console.warn('Registration table save failed:', regErr);
            }

            // 3) logins table
            try {
                await createLogin({
                    username: email,
                    passwordHash: password, // plain for now — Spring Security will hash later
                    lastPasswordDate: when,
                    lastLoginDate: when,
                });
            } catch (loginErr) {
                console.warn('Login table save failed:', loginErr);
            }

            login(customer);
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

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        const email = form.email.trim().toLowerCase();
        const password = form.password;
        const when = nowLocalDateTime();

        try {
            // 1) Check registrations (email + password)
            let regOk = false;
            try {
                const regRes = await api.get('/api/registrations');
                const regs = Array.isArray(regRes.data) ? regRes.data : [];
                const match = regs.find(
                    (r) =>
                        r.email?.toLowerCase() === email &&
                        r.password === password
                );
                regOk = !!match;
            } catch {
                // If registrations empty/unavailable, fall back to customer-only
                regOk = true;
            }

            if (!regOk) {
                setError('Invalid email or password.');
                setLoading(false);
                return;
            }

            // 2) Load customer profile
            const res = await api.get('/api/customers');
            const customers = Array.isArray(res.data) ? res.data : [];
            const found = customers.find(
                (c) => c.email?.toLowerCase() === email
            );
            if (!found) {
                setError('No customer profile found. Please register first.');
                setLoading(false);
                return;
            }

            // 3) Record login in logins table
            try {
                await createLogin({
                    username: email,
                    passwordHash: password,
                    lastPasswordDate: when,
                    lastLoginDate: when,
                });
            } catch (loginErr) {
                console.warn('Login table save failed:', loginErr);
            }

            login(found);
            navigate('/');
        } catch (err) {
            setError('Could not connect to the server!');
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

            <div className="card p-6 sm:p-8">
                <form
                    onSubmit={mode === 'login' ? handleLogin : handleRegister}
                    className="space-y-4"
                >
                    {mode === 'register' && (
                        <>
                            <div className="grid gap-4 sm:grid-cols-2">
                                <div>
                                    <label className="mb-1 block text-xs font-medium text-slate-600">
                                        First name
                                    </label>
                                    <input
                                        name="firstName"
                                        value={form.firstName}
                                        onChange={handleChange}
                                        required
                                        className="input-field"
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
                                        required
                                        className="input-field"
                                    />
                                </div>
                            </div>
                            <div>
                                <label className="mb-1 block text-xs font-medium text-slate-600">
                                    Phone
                                </label>
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
                                    Address
                                </label>
                                <input
                                    name="address"
                                    value={form.address}
                                    onChange={handleChange}
                                    className="input-field"
                                    placeholder="Optional"
                                />
                            </div>
                        </>
                    )}

                    <div>
                        <label className="mb-1 block text-xs font-medium text-slate-600">
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
                        <label className="mb-1 block text-xs font-medium text-slate-600">
                            Password
                        </label>
                        <input
                            type="password"
                            name="password"
                            value={form.password}
                            onChange={handleChange}
                            required
                            className="input-field"
                            placeholder={mode === 'register' ? 'Min 4 characters' : 'Your password'}
                            minLength={4}
                        />
                    </div>

                    {error && (
                        <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
                            {error}
                        </p>
                    )}

                    <button type="submit" disabled={loading} className="btn-primary w-full !py-3">
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