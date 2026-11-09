import { useEffect, useState } from 'react';
import {
    getProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    uploadProductImage,
    getOrders,
    updateOrderStatus,
} from '../../services/api';

const CATEGORIES = ['Laptop', 'Desktop', 'Phone', 'Monitor', 'Keyboard', 'Mouse', 'Accessory'];
const CONDITIONS = ['New', 'Second-hand'];
const ORDER_STATUSES = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

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

function formatDate(iso) {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('en-ZA', { dateStyle: 'medium', timeStyle: 'short' });
}

const statusStyles = {
    PENDING: 'bg-amber-100 text-amber-800',
    CONFIRMED: 'bg-sky-100 text-sky-800',
    SHIPPED: 'bg-violet-100 text-violet-800',
    DELIVERED: 'bg-emerald-100 text-emerald-800',
    CANCELLED: 'bg-red-100 text-red-800',
};

export function ProductsTab({ showMessage }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [saving, setSaving] = useState(false);
    const [uploading, setUploading] = useState(false);

    const load = () => {
        setLoading(true);
        getProducts()
            .then((data) => setProducts(Array.isArray(data) ? data : []))
            .catch(() => showMessage('error', 'Could not load products. Please try again.'))
            .finally(() => setLoading(false));
    };

    useEffect(load, []);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleFile = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setUploading(true);
        try {
            const result = await uploadProductImage(file);
            const imageUrl = result?.imageUrl || result;
            setForm((f) => ({ ...f, imageUrl: typeof imageUrl === 'string' ? imageUrl : '' }));
            showMessage('ok', 'Picture uploaded. Click Save to apply.');
        } catch {
            showMessage('error', 'Could not upload the picture.');
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
                const updated = await updateProduct(editingId, payload);
                showMessage('ok', `Product updated: ${updated?.name || payload.name}`);
            } else {
                const created = await createProduct(payload);
                showMessage('ok', `Product added: ${created?.name || payload.name}`);
            }
            resetForm();
            load();
        } catch (err) {
            showMessage('error', err.response?.data?.error || 'Could not save the product.');
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
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleDelete = async (p) => {
        if (!window.confirm(`Delete "${p.name}"?`)) return;
        try {
            await deleteProduct(p.id);
            if (editingId === p.id) resetForm();
            showMessage('ok', `Product deleted: ${p.name}`);
            load();
        } catch (err) {
            showMessage(
                'error',
                err.response?.data?.error ||
                (err.response?.status === 409
                    ? 'This product is in existing orders. Set stock to 0 instead.'
                    : 'Could not delete the product.')
            );
        }
    };

    const categoryOptions = CATEGORIES.includes(form.category) ? CATEGORIES : [...CATEGORIES, form.category];

    return (
        <>
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
                <textarea name="description" value={form.description} onChange={handleChange} rows={3} placeholder="Description" className="input-field" />
                <div className="flex flex-wrap items-center gap-4">
                    <div className="flex h-24 w-24 items-center justify-center overflow-hidden rounded-xl bg-slate-100 text-xs text-slate-400">
                        {form.imageUrl ? <img src={form.imageUrl} alt="" className="h-full w-full object-contain" /> : 'No picture'}
                    </div>
                    <div className="min-w-[16rem] flex-1 space-y-2">
                        <input type="file" accept="image/*" onChange={handleFile} className="text-sm" />
                        {uploading && <p className="text-xs text-slate-500">Uploading…</p>}
                        <input name="imageUrl" value={form.imageUrl} onChange={handleChange} placeholder="Or paste image path" className="input-field" />
                    </div>
                </div>
                <div className="flex flex-wrap gap-3">
                    <button type="submit" disabled={saving} className="btn-primary">
                        {saving ? 'Saving…' : editingId ? 'Save changes' : 'Add product'}
                    </button>
                    {editingId && (
                        <button type="button" onClick={resetForm} className="btn-secondary">Cancel edit</button>
                    )}
                </div>
            </form>

            <h2 className="mb-4 font-display text-lg font-bold text-slate-900">All products ({products.length})</h2>
            {loading ? (
                <p className="text-sm text-slate-500">Loading…</p>
            ) : products.length === 0 ? (
                <p className="text-sm text-slate-500">No products yet.</p>
            ) : (
                <div className="overflow-x-auto rounded-2xl border border-slate-200">
                    <table className="min-w-full text-left text-sm">
                        <thead className="bg-slate-50 text-xs uppercase text-slate-500">
                        <tr>
                            <th className="px-4 py-3">ID</th>
                            <th className="px-4 py-3">Name</th>
                            <th className="px-4 py-3">Brand</th>
                            <th className="px-4 py-3">Price</th>
                            <th className="px-4 py-3">Stock</th>
                            <th className="px-4 py-3">Category</th>
                            <th className="px-4 py-3">Actions</th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                        {products.map((p) => (
                            <tr key={p.id} className="bg-white hover:bg-slate-50">
                                <td className="px-4 py-3 font-mono text-xs text-slate-500">{p.id}</td>
                                <td className="px-4 py-3 font-medium text-slate-900">{p.name}</td>
                                <td className="px-4 py-3">{p.brand}</td>
                                <td className="px-4 py-3">{formatPrice(p.price)}</td>
                                <td className="px-4 py-3">{p.stockQuantity}</td>
                                <td className="px-4 py-3">{p.category}</td>
                                <td className="px-4 py-3">
                                    <div className="flex gap-2">
                                        <button type="button" onClick={() => startEdit(p)} className="text-xs font-semibold text-brand-600 hover:underline">Edit</button>
                                        <button type="button" onClick={() => handleDelete(p)} className="text-xs font-semibold text-red-600 hover:underline">Delete</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </>
    );
}

export function OrdersTab({ showMessage }) {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [updatingId, setUpdatingId] = useState(null);

    const load = () => {
        setLoading(true);
        getOrders()
            .then((data) => setOrders(Array.isArray(data) ? data : []))
            .catch(() => showMessage('error', 'Could not load orders. Please try again.'))
            .finally(() => setLoading(false));
    };

    useEffect(load, []);

    const handleStatusChange = async (orderId, status) => {
        setUpdatingId(orderId);
        try {
            await updateOrderStatus(orderId, status);
            showMessage('ok', `Order #${orderId} marked as ${status}`);
            load();
        } catch (err) {
            showMessage('error', err.response?.data?.error || 'Could not update status.');
        } finally {
            setUpdatingId(null);
        }
    };

    if (loading) return <p className="text-sm text-slate-500">Loading…</p>;
    if (orders.length === 0) return <p className="text-sm text-slate-500">No orders yet.</p>;

    return (
        <div className="space-y-4">
            <p className="text-sm text-slate-500">
                Update status as you process each order: Confirmed → Shipped → Delivered.
            </p>
            {orders
                .slice()
                .sort((a, b) => (b.id || 0) - (a.id || 0))
                .map((order) => (
                    <div key={order.id} className="card p-5">
                        <div className="flex flex-wrap items-start justify-between gap-3">
                            <div>
                                <p className="font-semibold text-slate-900">Order #{order.id}</p>
                                <p className="text-xs text-slate-400">{formatDate(order.orderDate)}</p>
                                <p className="mt-1 text-sm text-slate-600">
                                    {order.customer
                                        ? `${order.customer.firstName || ''} ${order.customer.lastName || ''} · ${order.customer.email || ''}`
                                        : 'Customer'}
                                </p>
                                {order.shippingAddress && (
                                    <p className="mt-1 text-sm text-slate-500">Ship to: {order.shippingAddress}</p>
                                )}
                            </div>
                            <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${statusStyles[order.status] || 'bg-slate-100'}`}>
                                {order.status}
                            </span>
                        </div>
                        <ul className="mt-3 space-y-1 text-sm text-slate-700">
                            {(order.orderItems || []).map((item, i) => (
                                <li key={i}>
                                    {item.product?.name || item.productName || 'Item'} × {item.quantity}
                                </li>
                            ))}
                        </ul>
                        <div className="mt-3 flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 pt-3">
                            <p className="font-display font-bold text-slate-900">{formatPrice(order.totalAmount)}</p>
                            <div className="flex items-center gap-2">
                                <label className="text-xs text-slate-500">Status</label>
                                <select
                                    value={order.status || 'PENDING'}
                                    disabled={updatingId === order.id}
                                    onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                    className="input-field !py-1.5 !text-sm"
                                >
                                    {ORDER_STATUSES.map((s) => (
                                        <option key={s} value={s}>{s}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>
                ))}
        </div>
    );
}

