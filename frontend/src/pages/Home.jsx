import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { getProducts } from '../services/api';
import ProductCard from '../components/ProductCard';
import LoadingSpinner from '../components/LoadingSpinner';

const CATEGORIES = [
    { name: 'Laptop', icon: '💻', desc: 'Powerful machines' },
    { name: 'Phone', icon: '📱', desc: 'Latest smartphones' },
    { name: 'Desktop', icon: '🖥️', desc: 'Towers & all-in-ones' },
    { name: 'Monitor', icon: '📺', desc: 'Crystal displays' },
    { name: 'Keyboard', icon: '⌨️', desc: 'Mechanical & more' },
    { name: 'Mouse', icon: '🖱️', desc: 'Precision gear' },
    { name: 'Accessory', icon: '🎧', desc: 'Everything else' },
];

export default function Home() {
    const [featured, setFeatured] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getProducts()
            .then((data) => {
                const list = Array.isArray(data) ? data : [];
                setFeatured(list.slice(0, 8));
            })
            .catch(() => setFeatured([]))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="animate-fade-in">
            {/* Hero */}
            <section className="relative overflow-hidden bg-gradient-to-br from-surface-900 via-brand-950 to-surface-900 text-white">
                <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,rgba(92,124,250,0.25),transparent_50%)]" />
                <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_bottom_left,rgba(6,182,212,0.15),transparent_50%)]" />
                <div className="relative mx-auto max-w-7xl px-4 py-20 sm:px-6 sm:py-28 lg:px-8">
                    <div className="max-w-2xl">
            <span className="inline-flex items-center gap-1.5 rounded-full bg-white/10 px-3 py-1 text-xs font-medium text-accent-400 backdrop-blur">
              <span className="h-1.5 w-1.5 rounded-full bg-accent-400 animate-pulse" />
              New season tech drops
            </span>
                        <h1 className="mt-5 font-display text-4xl font-extrabold tracking-tight sm:text-5xl lg:text-6xl">
                            Elevate your setup
                            <span className="block bg-gradient-to-r from-brand-400 to-accent-400 bg-clip-text text-transparent">
                with TechVault
              </span>
                        </h1>
                        <p className="mt-5 text-lg text-slate-300 leading-relaxed">
                            Discover new and second-hand laptops, desktops, phones, monitors and accessories.
                            Fast delivery, genuine products, and prices that make sense.
                        </p>
                        <div className="mt-8 flex flex-wrap gap-3">
                            <Link to="/products" className="btn-primary !px-7 !py-3 text-base shadow-glow">
                                Shop now
                                <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                                </svg>
                            </Link>
                            <Link to="/products" className="btn-secondary !bg-white/10 !border-white/20 !text-white hover:!bg-white/20 !px-7 !py-3 text-base">
                                Browse products
                            </Link>
                        </div>
                    </div>
                </div>
            </section>

            {/* Categories */}
            <section className="mx-auto max-w-7xl px-4 py-14 sm:px-6 lg:px-8">
                <div className="mb-8 flex items-end justify-between">
                    <div>
                        <h2 className="font-display text-2xl font-bold text-slate-900">Shop by category</h2>
                        <p className="mt-1 text-sm text-slate-500">Find exactly what you need</p>
                    </div>
                </div>
                <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-6">
                    {CATEGORIES.map((cat) => (
                        <Link
                            key={cat.name}
                            to={`/products?category=${cat.name}`}
                            className="card group flex flex-col items-center gap-2 p-5 text-center hover:-translate-y-1"
                        >
                            <span className="text-3xl transition group-hover:scale-110">{cat.icon}</span>
                            <span className="text-sm font-semibold text-slate-800">{cat.name}</span>
                            <span className="text-[11px] text-slate-400">{cat.desc}</span>
                        </Link>
                    ))}
                </div>
            </section>

            {/* Featured */}
            <section className="bg-white border-y border-slate-100">
                <div className="mx-auto max-w-7xl px-4 py-14 sm:px-6 lg:px-8">
                    <div className="mb-8 flex items-end justify-between">
                        <div>
                            <h2 className="font-display text-2xl font-bold text-slate-900">Featured products</h2>
                            <p className="mt-1 text-sm text-slate-500">Hand-picked tech everyone loves</p>
                        </div>
                        <Link to="/products" className="text-sm font-semibold text-brand-600 hover:text-brand-700">
                            View all →
                        </Link>
                    </div>

                    {loading ? (
                        <LoadingSpinner label="Loading products..." />
                    ) : featured.length === 0 ? (
                        <div className="rounded-2xl border border-dashed border-slate-200 py-16 text-center">
                            <p className="text-slate-500">No products yet. Start the Spring Boot backend to see data.</p>
                            <p className="mt-2 text-xs text-slate-400">API: http://localhost:8080/api/products</p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
                            {featured.map((p) => (
                                <ProductCard key={p.id} product={p} />
                            ))}
                        </div>
                    )}
                </div>
            </section>

            {/* Trust strip */}
            <section className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
                <div className="grid gap-6 sm:grid-cols-3">
                    {[
                        { title: 'Genuine products', desc: 'Sourced from trusted brands only', icon: '✓' },
                        { title: 'Secure checkout', desc: 'Your data is always protected', icon: '🔒' },
                        { title: 'Fast delivery', desc: 'Across South Africa', icon: '🚚' },
                    ].map((item) => (
                        <div key={item.title} className="flex items-start gap-4 rounded-2xl bg-brand-50/60 p-5">
              <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-brand-600 text-lg text-white">
                {item.icon}
              </span>
                            <div>
                                <h3 className="font-semibold text-slate-900">{item.title}</h3>
                                <p className="text-sm text-slate-500">{item.desc}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </section>
        </div>
    );
}
