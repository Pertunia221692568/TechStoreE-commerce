import { useEffect, useState } from 'react';
import {
    getProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    uploadProductImage,
} from '../services/api';

// Temporary admin sign-in. This is NOT real security we will replace it with proper authentication once security has been implemented

const ADMIN_USER = 'admin';
const ADMIN_PASS = 'admin123';

const CATEGORIES = ['Laptop', 'Desktop', 'Phone', 'Monitor', 'Keyboard', 'Mouse', 'Accessory'];
const CONDITIONS = ['New', 'Second-hand'];

const emptyForm = {
    name: '',
    brand: '',
    description: '',
    price: '',
    stockQuantity: '',
    category: 'Laptop',
    itemCondition: 'New',
    imageUrl: '',
};

function formatPrice(price) {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(Number(price));
}

function AdminLogin({ onSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (username === ADMIN_USER && password === ADMIN_PASS) {
            sessionStorage.setItem('techvault_admin', 'yes');
            onSuccess();
        } else {
            setError('Wrong admin username or password.');
        }
    };

    return (
        <div className="mx-auto flex min-h-[60vh] max-w-sm flex-col justify-center px-4 py-12">
            <h1 className="font-display text-2xl font-bold text-slate-900">Admin sign in</h1>
            <p className="mt-1 text-sm text-slate-500">Manage the TechVault products.</p>
            <form onSubmit={handleSubmit} className="mt-6 space-y-3">
                <input
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Username"
                    className="input-field"
                    required
                />
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Password"
                    className="input-field"
                    required
                />
                {error && <p className="text-sm text-red-600">{error}</p>}
                <button type="submit" className="btn-primary w-full">Sign in</button>
            </form>
        </div>
    );
}

function AdminPanel({ onLogout }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [saving, setSaving] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [message, setMessage] = useState(null); // { type: 'ok' | 'error', text }

    const load = () => {
        setLoading(true);
        getProducts()
            .then((data) => setProducts(Array.isArray(data) ? data : []))
            .catch(() => setMessage({ type: 'error', text: 'Could not load products. Is the backend running?' }))
            .finally(() => setLoading(false));
    };

    useEffect(load, []);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleFile = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setUploading(true);
        setMessage(null);
        try {
            const { imageUrl } = await uploadProductImage(file);
            setForm((f) => ({ ...f, imageUrl }));
        } catch (err) {
            setMessage({ type: 'error', text: err.response?.data?.error || 'Could not upload the picture.' });
        } finally {
            setUploading(false);
            e.target.value = '';
        }
    };

    const resetForm = () => {
        setForm(emptyForm);
        setEditingId(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setMessage(null);
        const payload = {
            name: form.name.trim(),
            brand: form.brand.trim(),
            description: form.description.trim(),
            price: Number(form.price),
            stockQuantity: Number(form.stockQuantity),
            category: form.category,
            itemCondition: form.itemCondition,
            imageUrl: form.imageUrl.trim() || null,
        };
        try {
            if (editingId) {
                await updateProduct(editingId, payload);
                setMessage({ type: 'ok', text: 'Product updated.' });
            } else {
                await createProduct(payload);
                setMessage({ type: 'ok', text: 'Product added.' });
            }
            resetForm();
            load();
        } catch (err) {
            setMessage({ type: 'error', text: err.response?.data?.error || 'Could not save the product.' });
        } finally {
            setSaving(false);
        }
    };

    const startEdit = (p) => {
        setEditingId(p.id);
        setForm({
            name: p.name || '',
            brand: p.brand || '',
            description: p.description || '',
            price: String(p.price ?? ''),
            stockQuantity: String(p.stockQuantity ?? ''),
            category: p.category || 'Laptop',
            itemCondition: p.itemCondition || 'New',
            imageUrl: p.imageUrl || '',
        });
        setMessage(null);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleDelete = async (p) => {
        if (!window.confirm(`Delete "${p.name}"?`)) return;
        setMessage(null);
        try {
            await deleteProduct(p.id);
            if (editingId === p.id) resetForm();
            setMessage({ type: 'ok', text: 'Product deleted.' });
            load();
        } catch (err) {
            setMessage({ type: 'error', text: err.response?.data?.error || 'Could not delete the product.' });
        }
    };

    const categoryOptions = CATEGORIES.includes(form.category) ? CATEGORIES : [...CATEGORIES, form.category];

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
            <div className="mb-6 flex items-center justify-between">
                <h1 className="font-display text-3xl font-bold text-slate-900">Admin: products</h1>
                <button onClick={onLogout} className="btn-secondary !px-3 !py-1.5 text-xs">Sign out</button>
            </div>

            {message && (
                <p
                    className={`mb-4 rounded-lg px-3 py-2 text-sm ${
                        message.type === 'ok' ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-700'
                    }`}
                >
                    {message.text}
                </p>
            )}

            {/* Add / edit form */}
            <form onSubmit={handleSubmit} className="card mb-10 space-y-4 p-6">
                <h2 className="font-display text-lg font-bold text-slate-900">
                    {editingId ? `Edit product #${editingId}` : 'Add a new product'}
                </h2>

                <div className="grid gap-4 sm:grid-cols-2">
                    <input name="name" value={form.name} onChange={handleChange} placeholder="Product name" className="input-field" required />
                    <input name="brand" value={form.brand} onChange={handleChange} placeholder="Brand" className="input-field" required />
                    <input name="price" type="number" min="0" step="0.01" value={form.price} onChange={handleChange} placeholder="Price (ZAR)" className="input-field" required />
                    <input name="stockQuantity" type="number" min="0" step="1" value={form.stockQuantity} onChange={handleChange} placeholder="Stock quantity" className="input-field" required />
                    <select name="category" value={form.category} onChange={handleChange} className="input-field">
                        {categoryOptions.map((c) => <option key={c} value={c}>{c}</option>)}
                    </select>
                    <select name="itemCondition" value={form.itemCondition} onChange={handleChange} className="input-field">
                        {CONDITIONS.map((c) => <option key={c} value={c}>{c}</option>)}
                    </select>
                </div>

                <textarea
                    name="description"
                    value={form.description}
                    onChange={handleChange}
                    rows={3}
                    placeholder="Description (specs, condition notes...)"
                    className="input-field"
                />

                <div className="flex flex-wrap items-center gap-4">
                    <div className="flex h-24 w-24 items-center justify-center overflow-hidden rounded-xl bg-slate-100 text-xs text-slate-400">
                        {form.imageUrl ? (
                            <img src={form.imageUrl} alt="Preview" className="h-full w-full object-contain" />
                        ) : (
                            'No picture'
                        )}
                    </div>
                    <div className="min-w-[16rem] flex-1 space-y-2">
                        <input type="file" accept="image/*" onChange={handleFile} className="text-sm" />
                        {uploading && <p className="text-xs text-slate-500">Uploading...</p>}
                        <input
                            name="imageUrl"
                            value={form.imageUrl}
                            onChange={handleChange}
                            placeholder="...or a picture path, e.g. /images/products/dell-xps-13.jpg"
                            className="input-field"
                        />
                    </div>
                </div>

                <div className="flex gap-3">
                    <button type="submit" disabled={saving || uploading} className="btn-primary">
                        {saving ? 'Saving...' : editingId ? 'Save changes' : 'Add product'}
                    </button>
                    {editingId && (
                        <button type="button" onClick={resetForm} className="btn-secondary">Cancel</button>
                    )}
                </div>
            </form>

            {/* Product list */}
            <h2 className="mb-3 font-display text-lg font-bold text-slate-900">
                All products {!loading && <span className="text-slate-400">({products.length})</span>}
            </h2>
            <div className="card overflow-x-auto">
                <table className="w-full text-left text-sm">
                    <thead className="border-b border-slate-100 text-xs uppercase tracking-wide text-slate-400">
                    <tr>
                        <th className="p-3">Picture</th>
                        <th className="p-3">Product</th>
                        <th className="p-3">Category</th>
                        <th className="p-3">Condition</th>
                        <th className="p-3">Price</th>
                        <th className="p-3">Stock</th>
                        <th className="p-3"></th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                    {products.map((p) => (
                        <tr key={p.id}>
                            <td className="p-3">
                                <div className="flex h-12 w-12 items-center justify-center overflow-hidden rounded-lg bg-slate-100">
                                    {p.imageUrl && (
                                        <img
                                            src={p.imageUrl}
                                            alt=""
                                            className="h-full w-full object-contain"
                                            onError={(e) => { e.currentTarget.style.display = 'none'; }}
                                        />
                                    )}
                                </div>
                            </td>
                            <td className="p-3">
                                <p className="font-medium text-slate-900">{p.name}</p>
                                <p className="text-xs text-slate-400">{p.brand}</p>
                            </td>
                            <td className="p-3">{p.category}</td>
                            <td className="p-3">{p.itemCondition || '—'}</td>
                            <td className="p-3">{formatPrice(p.price)}</td>
                            <td className="p-3">{p.stockQuantity}</td>
                            <td className="whitespace-nowrap p-3 text-right">
                                <button onClick={() => startEdit(p)} className="mr-3 text-xs font-medium text-brand-600 hover:text-brand-700">Edit</button>
                                <button onClick={() => handleDelete(p)} className="text-xs font-medium text-red-600 hover:text-red-700">Delete</button>
                            </td>
                        </tr>
                    ))}
                    {!loading && products.length === 0 && (
                        <tr><td colSpan={7} className="p-6 text-center text-slate-400">No products yet.</td></tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default function Admin() {
    const [isAdmin, setIsAdmin] = useState(() => sessionStorage.getItem('techvault_admin') === 'yes');

    const logout = () => {
        sessionStorage.removeItem('techvault_admin');
        setIsAdmin(false);
    };

    return isAdmin ? <AdminPanel onLogout={logout} /> : <AdminLogin onSuccess={() => setIsAdmin(true)} />;
}
