import { useEffect, useMemo, useState } from 'react';
import { getProducts, createProduct, updateProduct, deleteProduct, uploadProductImage } from '../../services/api';
import LoadingSpinner from '../../components/LoadingSpinner';
import { PageHeader, Modal, Notice, useNotice, Empty, Th, Td, money } from '../../components/admin/ui';

const CATEGORIES = ['Laptop', 'Desktop', 'Phone', 'Monitor', 'Keyboard', 'Mouse', 'Accessory'];
const CONDITIONS = ['New', 'Second-hand'];
const empty = { name: '', brand: '', category: 'Laptop', itemCondition: 'New', price: '', stockQuantity: '', description: '', imageUrl: '' };

export default function AdminProducts() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [q, setQ] = useState('');
    const [cat, setCat] = useState('All');
    const [form, setForm] = useState(null);       // null = closed
    const [editingId, setEditingId] = useState(null);
    const [saving, setSaving] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [notice, show] = useNotice();

    const load = () =>
        getProducts().then((d) => setProducts(Array.isArray(d) ? d : [])).catch(() => show('error', 'Could not load products.')).finally(() => setLoading(false));
    useEffect(() => { load(); }, []);

    const shown = useMemo(() => products.filter((p) =>
        (cat === 'All' || p.category === cat) &&
        `${p.name} ${p.brand}`.toLowerCase().includes(q.toLowerCase())), [products, q, cat]);

    const openNew = () => { setEditingId(null); setForm({ ...empty }); };
    const openEdit = (p) => {
        setEditingId(p.id);
        setForm({ name: p.name, brand: p.brand, category: p.category || 'Laptop', itemCondition: p.itemCondition || 'New',
            price: p.price, stockQuantity: p.stockQuantity, description: p.description || '', imageUrl: p.imageUrl || '' });
    };
    const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

    const onFile = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setUploading(true);
        try {
            const r = await uploadProductImage(file);
            const url = r?.imageUrl || r?.url || (typeof r === 'string' ? r : '');
            setForm((f) => ({ ...f, imageUrl: url }));
        } catch (err) {
            show('error', err.response?.data?.error || 'Could not upload the picture.');
        } finally {
            setUploading(false);
            e.target.value = '';
        }
    };

    const save = async (e) => {
        e.preventDefault();
        if (Number(form.price) < 0 || Number(form.stockQuantity) < 0) return show('error', 'Price and stock cannot be negative.');
        setSaving(true);
        try {
            const body = { ...form, price: Number(form.price), stockQuantity: Number(form.stockQuantity) };
            if (editingId) await updateProduct(editingId, body); else await createProduct(body);
            show('ok', editingId ? 'Product updated.' : 'Product added.');
            setForm(null);
            load();
        } catch (err) {
            show('error', err.response?.data?.error || 'Could not save the product.');
        } finally {
            setSaving(false);
        }
    };

    const remove = async (p) => {
        if (!window.confirm(`Delete "${p.name}"? This cannot be undone.`)) return;
        try {
            await deleteProduct(p.id);
            show('ok', 'Product deleted.');
            load();
        } catch (err) {
            show('error', err.response?.data?.error || 'Could not delete the product.');
        }
    };

    if (loading) return <LoadingSpinner label="Loading products..." />;

    return (
        <div className="animate-fade-in">
            <PageHeader
                title="Products"
                subtitle={`${products.length} products in the catalogue`}
                action={<button onClick={openNew} className="btn-primary">+ Add product</button>}
            />
            <Notice notice={notice} />

            <div className="mb-4 flex flex-wrap gap-3">
                <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search name or brand..." className="input-field max-w-xs" />
                <select value={cat} onChange={(e) => setCat(e.target.value)} className="input-field max-w-[11rem]">
                    <option>All</option>
                    {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
                </select>
            </div>

            {shown.length === 0 ? <Empty text="No products match." /> : (
                <div className="card overflow-x-auto">
                    <table className="w-full text-sm">
                        <thead className="border-b border-slate-100 bg-slate-50"><tr>
                            <Th>Product</Th><Th>Category</Th><Th>Condition</Th><Th>Price</Th><Th>Stock</Th><Th className="text-right">Actions</Th>
                        </tr></thead>
                        <tbody className="divide-y divide-slate-100">
                        {shown.map((p) => (
                            <tr key={p.id} className="hover:bg-slate-50/60">
                                <Td>
                                    <div className="flex items-center gap-3">
                                        <div className="flex h-12 w-12 shrink-0 items-center justify-center overflow-hidden rounded-xl bg-slate-100">
                                            {p.imageUrl ? <img src={p.imageUrl} alt="" className="h-full w-full object-contain" /> : <span className="text-[10px] text-slate-400">No img</span>}
                                        </div>
                                        <div className="min-w-0">
                                            <p className="truncate font-semibold text-slate-900">{p.name}</p>
                                            <p className="text-xs text-slate-400">{p.brand}</p>
                                        </div>
                                    </div>
                                </Td>
                                <Td>{p.category}</Td>
                                <Td>{p.itemCondition}</Td>
                                <Td className="font-medium">{money(p.price)}</Td>
                                <Td>
                                        <span className={`rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                                            p.stockQuantity === 0 ? 'bg-red-100 text-red-700' : p.stockQuantity <= 5 ? 'bg-amber-100 text-amber-800' : 'bg-emerald-100 text-emerald-800'}`}>
                                            {p.stockQuantity === 0 ? 'Out of stock' : `${p.stockQuantity} in stock`}
                                        </span>
                                </Td>
                                <Td className="text-right whitespace-nowrap">
                                    <button onClick={() => openEdit(p)} className="mr-2 rounded-lg border border-slate-200 px-3 py-1 text-xs font-semibold text-slate-700 hover:bg-slate-50">Edit</button>
                                    <button onClick={() => remove(p)} className="rounded-lg border border-red-200 px-3 py-1 text-xs font-semibold text-red-600 hover:bg-red-50">Delete</button>
                                </Td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {form && (
                <Modal title={editingId ? 'Edit product' : 'Add product'} onClose={() => setForm(null)}>
                    <form onSubmit={save} className="space-y-3">
                        <div className="grid gap-3 sm:grid-cols-2">
                            <input className="input-field" placeholder="Name" value={form.name} onChange={set('name')} required />
                            <input className="input-field" placeholder="Brand" value={form.brand} onChange={set('brand')} required />
                            <select className="input-field" value={form.category} onChange={set('category')}>{CATEGORIES.map((c) => <option key={c}>{c}</option>)}</select>
                            <select className="input-field" value={form.itemCondition} onChange={set('itemCondition')}>{CONDITIONS.map((c) => <option key={c}>{c}</option>)}</select>
                            <input className="input-field" type="number" min="0" step="0.01" placeholder="Price (R)" value={form.price} onChange={set('price')} required />
                            <input className="input-field" type="number" min="0" step="1" placeholder="Stock quantity" value={form.stockQuantity} onChange={set('stockQuantity')} required />
                        </div>
                        <textarea className="input-field min-h-[90px]" placeholder="Description (shown when customers hover the product)" value={form.description} onChange={set('description')} />
                        <div className="flex items-center gap-3 rounded-xl border border-dashed border-slate-300 p-3">
                            <div className="flex h-16 w-16 shrink-0 items-center justify-center overflow-hidden rounded-lg bg-slate-100">
                                {form.imageUrl ? <img src={form.imageUrl} alt="" className="h-full w-full object-contain" /> : <span className="text-[10px] text-slate-400">No picture</span>}
                            </div>
                            <label className="cursor-pointer text-sm font-medium text-brand-600 hover:underline">
                                {uploading ? 'Uploading…' : 'Upload a picture'}
                                <input type="file" accept="image/*" className="hidden" onChange={onFile} disabled={uploading} />
                            </label>
                        </div>
                        <div className="flex justify-end gap-2 pt-2">
                            <button type="button" onClick={() => setForm(null)} className="btn-secondary">Cancel</button>
                            <button type="submit" disabled={saving || uploading} className="btn-primary">{saving ? 'Saving…' : 'Save product'}</button>
                        </div>
                    </form>
                </Modal>
            )}
        </div>
    );
}
