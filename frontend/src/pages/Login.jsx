import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerCustomer, getProducts } from '../services/api';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

export default function Login() {
  const [mode, setMode] = useState('login'); // login | register
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
    try {
      const customer = await registerCustomer({
        firstName: form.firstName,
        lastName: form.lastName,
        email: form.email,
        phone: form.phone || '',
        address: form.address || '',
      });
      login(customer);
      navigate('/');
    } catch (err) {
      const msg = err.response?.data?.error || err.response?.data?.message || 'Registration failed';
      setError(typeof msg === 'string' ? msg : 'Registration failed. Email may already exist.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      // Backend has no dedicated login-by-email endpoint that returns customer,
      // so we fetch all customers and match by email (fine for student project).
      const res = await api.get('/api/customers');
      const customers = Array.isArray(res.data) ? res.data : [];
      const found = customers.find(
        (c) => c.email?.toLowerCase() === form.email.trim().toLowerCase()
      );
      if (!found) {
        setError('No account found with that email. Please register first.');
        setLoading(false);
        return;
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
    <div className="mx-auto flex min-h-[70vh] max-w-md flex-col justify-center px-4 py-12 animate-fade-in">
      <div className="text-center mb-8">
        <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-600 to-brand-800 shadow-glow">
          <svg className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
          </svg>
        </div>
        <h1 className="font-display text-2xl font-bold text-slate-900">
          {mode === 'login' ? 'Welcome back' : 'Create account'}
        </h1>
        <p className="mt-1 text-sm text-slate-500">
          {mode === 'login' ? 'Sign in to continue shopping' : 'Join TechVault today'}
        </p>
      </div>

      <div className="card p-6">
        {/* Tabs */}
        <div className="mb-6 flex rounded-xl bg-slate-100 p-1">
          <button
            type="button"
            onClick={() => { setMode('login'); setError(''); }}
            className={`flex-1 rounded-lg py-2 text-sm font-semibold transition ${
              mode === 'login' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'
            }`}
          >
            Sign in
          </button>
          <button
            type="button"
            onClick={() => { setMode('register'); setError(''); }}
            className={`flex-1 rounded-lg py-2 text-sm font-semibold transition ${
              mode === 'register' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'
            }`}
          >
            Register
          </button>
        </div>

        <form onSubmit={mode === 'login' ? handleLogin : handleRegister} className="space-y-4">
          {mode === 'register' && (
            <>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="mb-1 block text-xs font-medium text-slate-600">First name</label>
                  <input name="firstName" value={form.firstName} onChange={handleChange} required className="input-field" />
                </div>
                <div>
                  <label className="mb-1 block text-xs font-medium text-slate-600">Last name</label>
                  <input name="lastName" value={form.lastName} onChange={handleChange} required className="input-field" />
                </div>
              </div>
              <div>
                <label className="mb-1 block text-xs font-medium text-slate-600">Phone</label>
                <input name="phone" value={form.phone} onChange={handleChange} className="input-field" placeholder="Optional" />
              </div>
              <div>
                <label className="mb-1 block text-xs font-medium text-slate-600">Address</label>
                <input name="address" value={form.address} onChange={handleChange} className="input-field" placeholder="Optional" />
              </div>
            </>
          )}

          <div>
            <label className="mb-1 block text-xs font-medium text-slate-600">Email</label>
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

          {mode === 'register' && (
            <p className="text-xs text-slate-400">
              Password is not required by the current backend API. Account is created with email only.
            </p>
          )}

          {error && (
            <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>
          )}

          <button type="submit" disabled={loading} className="btn-primary w-full !py-3">
            {loading ? 'Please wait...' : mode === 'login' ? 'Sign in' : 'Create account'}
          </button>
        </form>
      </div>

      <p className="mt-6 text-center text-sm text-slate-500">
        <Link to="/" className="font-medium text-brand-600 hover:text-brand-700">← Back to home</Link>
      </p>
    </div>
  );
}
