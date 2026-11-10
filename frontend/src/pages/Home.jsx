import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { getProducts } from '../services/api';
import ProductCard from '../components/ProductCard';
import LoadingSpinner from '../components/LoadingSpinner';
import Logo from '../components/Logo';
import TechBackdrop from '../components/TechBackdrop';

const TYPED_WORDS = ['laptops', 'phones', 'desktops', 'monitors', 'gaming gear'];
const CATEGORIES = ['Laptop', 'Desktop', 'Phone', 'Monitor', 'Keyboard', 'Mouse', 'Accessory'];
const BRANDS = ['Dell', 'HP', 'Lenovo', 'Apple', 'Samsung', 'Logitech', 'Razer', 'LG', 'Sony', 'Keychron'];

// Types one word at a time, deletes it, then moves to the next
function useTypewriter(words) {
    const [text, setText] = useState('');
    const [index, setIndex] = useState(0);
    const [deleting, setDeleting] = useState(false);

    useEffect(() => {
        const word = words[index % words.length];
        const delay = deleting ? 45 : text === word ? 1400 : 90;
        const t = setTimeout(() => {
            if (!deleting && text === word) setDeleting(true);
            else if (deleting && text === '') {
                setDeleting(false);
                setIndex((i) => i + 1);
            } else {
                setText(deleting ? word.slice(0, text.length - 1) : word.slice(0, text.length + 1));
            }
        }, delay);
        return () => clearTimeout(t);
    }, [text, deleting, index, words]);

    return text;
}

export default function Home() {
    const [featured, setFeatured] = useState([]);
    const [loading, setLoading] = useState(true);

    // Simulated daily shipping capacity (unique differentiator)
    // Backend can later expose GET /api/shipping/capacity
    const DAILY_CAPACITY = 50;
    const typed = useTypewriter(TYPED_WORDS);
    const [spot, setSpot] = useState({ x: 70, y: 30 });
    const [slotsUsed] = useState(() => Math.floor(Math.random() * 18) + 8);

    useEffect(() => {
        getProducts()
            .then((data) => {
                const list = Array.isArray(data) ? data : [];
                setFeatured(list.slice(0, 8));
            })
            .catch(() => setFeatured([]))
            .finally(() => setLoading(false));
    }, []);

    const capacityPercent = Math.min(100, Math.round((slotsUsed / DAILY_CAPACITY) * 100));

    return (
        <div className="animate-fade-in">
            {/* Hero – screams Tech Store immediately */}
            <section className="relative overflow-hidden bg-gradient-to-br from-surface-900 via-brand-950 to-surface-900 text-white"
                     onMouseMove={(e) => {
                         const r = e.currentTarget.getBoundingClientRect();
                         setSpot({ x: ((e.clientX - r.left) / r.width) * 100, y: ((e.clientY - r.top) / r.height) * 100 });
                     }}
            >
                <TechBackdrop />
                <div
                    className="absolute inset-0"
                    style={{ background: `radial-gradient(500px circle at ${spot.x}% ${spot.y}%, rgba(34,211,238,0.16), transparent 60%)` }}
                />
                <div className="absolute inset-0 bg-gradient-to-r from-surface-950/90 via-surface-950/50 to-transparent" />
                <div
                    className="absolute inset-0 opacity-[0.06]"
                    style={{
                        backgroundImage: `linear-gradient(rgba(255,255,255,.6) 1px, transparent 1px),
                                          linear-gradient(90deg, rgba(255,255,255,.6) 1px, transparent 1px)`,
                        backgroundSize: '48px 48px',
                    }}
                />
                <div className="absolute inset-x-0 top-0 h-24 animate-scan bg-gradient-to-b from-transparent via-accent-400/10 to-transparent" />

                <div className="relative mx-auto max-w-7xl px-4 py-16 sm:px-6 sm:py-24 lg:px-8">
                    <div className="flex flex-col items-start gap-10 lg:flex-row lg:items-center lg:justify-between">
                        <div className="max-w-2xl">
                            <div className="mb-5 flex items-center gap-3">
                                <Logo size="lg" showWordmark={false} to={null} />
                                <span className="inline-flex items-center gap-1.5 rounded-full bg-white/10 px-3 py-1 text-xs font-medium text-accent-400 backdrop-blur">
                                    <span className="h-1.5 w-1.5 rounded-full bg-accent-400 animate-pulse" />
                                    South Africa’s premium tech e-store
                                </span>
                            </div>

                            <h1 className="font-display text-4xl font-extrabold tracking-tight sm:text-5xl lg:text-6xl">
                                The tech store
                                <span className="block bg-gradient-to-r from-brand-400 to-accent-400 bg-clip-text text-transparent">
                                    that feels like tomorrow
                                </span>
                            </h1>

                            <p className="mt-5 font-mono text-sm text-accent-400 sm:text-base">
                                <span className="text-slate-500">&gt;</span> shop --new --certified{' '}
                                <span className="text-white">{typed}</span>
                                <span className="animate-blink">▌</span>
                            </p>

                            <p className="mt-4 max-w-xl text-base leading-relaxed text-slate-300 sm:text-lg">
                                New and certified pre-owned laptops, phones, desktops, monitors and gear.
                                Transparent pricing, genuine products, and delivery you can track.
                            </p>

                            <div className="mt-8 flex flex-wrap gap-3">
                                <Link to="/products" className="btn-primary !px-7 !py-3 text-base shadow-glow">
                                    Shop the catalogue
                                    <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                                    </svg>
                                </Link>
                                <Link
                                    to="/shipping"
                                    className="btn-secondary !border-white/20 !bg-white/10 !px-7 !py-3 text-base !text-white hover:!bg-white/20"
                                >
                                    Shipping & capacity
                                </Link>
                            </div>

                            <div className="mt-10 flex flex-wrap gap-6 text-sm">
                                <div>
                                    <p className="font-display text-2xl font-bold text-white">2 400+</p>
                                    <p className="text-slate-400">Devices sold</p>
                                </div>
                                <div>
                                    <p className="font-display text-2xl font-bold text-white">4.9★</p>
                                    <p className="text-slate-400">Customer rating</p>
                                </div>
                                <div>
                                    <p className="font-display text-2xl font-bold text-white">14 days</p>
                                    <p className="text-slate-400">Easy returns</p>
                                </div>
                            </div>
                        </div>

                        {/* Unique differentiator: Daily Shipping Capacity */}
                        <div className="w-full max-w-sm rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-md shadow-2xl">
                            <div className="flex items-center justify-between">
                                <h3 className="text-sm font-semibold uppercase tracking-wider text-accent-400">
                                    Today’s shipping capacity
                                </h3>
                                <span className="rounded-full bg-emerald-500/20 px-2 py-0.5 text-[11px] font-semibold text-emerald-300">
                                    Live
                                </span>
                            </div>
                            <p className="mt-3 font-display text-3xl font-bold text-white">
                                {slotsUsed}
                                <span className="text-lg font-medium text-slate-400"> / {DAILY_CAPACITY}</span>
                            </p>
                            <p className="mt-1 text-sm text-slate-400">
                                product units confirmed for dispatch today
                            </p>
                            <div className="mt-4 h-2.5 overflow-hidden rounded-full bg-white/10">
                                <div
                                    className="h-full rounded-full bg-gradient-to-r from-brand-500 to-accent-400 transition-all duration-700"
                                    style={{ width: `${capacityPercent}%` }}
                                />
                            </div>
                            <p className="mt-3 text-xs text-slate-400">
                                Orders placed now still ship within 1–2 business days while capacity remains.
                            </p>
                            <Link
                                to="/shipping"
                                className="mt-4 inline-flex text-sm font-semibold text-brand-300 hover:text-brand-200"
                            >
                                How shipping works →
                            </Link>
                        </div>
                    </div>
                </div>
            </section>

            {/* Shop by category + brand strip */}
            <section className="relative border-b border-white/5 bg-surface-950 text-white">
                <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
                    <p className="font-mono text-xs uppercase tracking-[0.25em] text-accent-400">&gt; browse --by-category</p>
                    <div className="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-4 lg:grid-cols-7">
                        {CATEGORIES.map((c, i) => (
                            <Link
                                key={c}
                                to={`/products?category=${c}`}
                                className="group rounded-xl border border-white/10 bg-white/5 p-4 transition-all duration-300 hover:-translate-y-1 hover:border-accent-400/60 hover:bg-brand-600/20 hover:shadow-glow"
                            >
                                <span className="font-mono text-[11px] text-slate-500 group-hover:text-accent-400">0{i + 1}</span>
                                <p className="mt-1 font-display text-base font-bold">{c}s</p>
                                <p className="mt-1 text-xs text-slate-400 transition group-hover:text-white">Browse →</p>
                            </Link>
                        ))}
                    </div>
                </div>
                <div className="overflow-hidden border-t border-white/5 py-4">
                    <div className="flex w-max animate-marquee gap-12 whitespace-nowrap font-display text-lg font-bold uppercase tracking-widest text-slate-600">
                        {[...BRANDS, ...BRANDS].map((b, i) => (
                            <span key={i} className="transition hover:text-accent-400">{b}</span>
                        ))}
                    </div>
                </div>
            </section>

            {/* Value props */}
            <section className="border-b border-slate-100 bg-white">
                <div className="mx-auto grid max-w-7xl gap-6 px-4 py-10 sm:grid-cols-2 sm:px-6 lg:grid-cols-4 lg:px-8">
                    {[
                        {
                            title: 'New + Certified Pre-owned',
                            desc: 'Every device inspected. Clear condition labels so you know exactly what you buy.',
                            icon: (
                                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            ),
                        },
                        {
                            title: 'Transparent pricing',
                            desc: 'No hidden fees. What you see at checkout is what you pay — including delivery.',
                            icon: (
                                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 18.75a60.07 60.07 0 0115.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 013 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 00-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 01-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 003 15h-.75M15 10.5a3 3 0 11-6 0 3 3 0 016 0zm3 0h.008v.008H18V10.5zm-12 0h.008v.008H6V10.5z" />
                                </svg>
                            ),
                        },
                        {
                            title: 'Tracked shipping',
                            desc: 'Every order gets a unique Shipping ID. Know exactly when your tech is on the way.',
                            icon: (
                                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m16.5 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 00-3.213-9.193 2.056 2.056 0 00-1.58-.73H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 00-10.026 0 1.106 1.106 0 00-.987 1.106v7.635m12-6.677v6.677m0 4.5v-4.5m0 0h-12" />
                                </svg>
                            ),
                        },
                        {
                            title: 'Secure card payments',
                            desc: 'Pay by card with CVV verification. Your payment details stay protected.',
                            icon: (
                                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 002.25-2.25V6.75A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25v10.5A2.25 2.25 0 004.5 19.5z" />
                                </svg>
                            ),
                        },
                    ].map((item) => (
                        <div key={item.title} className="flex gap-4">
                            <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-brand-50 text-brand-600">
                                {item.icon}
                            </div>
                            <div>
                                <h3 className="font-semibold text-slate-900">{item.title}</h3>
                                <p className="mt-1 text-sm leading-relaxed text-slate-500">{item.desc}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            {/* Featured products */}
            <section className="relative bg-gradient-to-b from-surface-950 to-surface-900 text-white">
                <div className="mx-auto max-w-7xl px-4 py-14 sm:px-6 lg:px-8">
                    <div className="mb-8 flex items-end justify-between">
                        <div>
                            <h2 className="font-display text-2xl font-bold text-white">Featured products</h2>
                            <p className="mt-1 text-sm text-slate-400">Hover a product to preview its specs</p>
                        </div>
                        <Link to="/products" className="text-sm font-semibold text-accent-400 hover:text-white">
                            View full catalogue →
                        </Link>
                    </div>

                    {loading ? (
                        <LoadingSpinner label="Loading products..." />
                    ) : featured.length === 0 ? (
                        <div className="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center">
                            <p className="text-slate-500">No products available right now. Please check back soon.</p>
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

            {/* CTA band */}
            <section className="relative overflow-hidden bg-gradient-to-r from-brand-700 to-brand-900 text-white">
                <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_right,rgba(34,211,238,0.2),transparent_60%)]" />
                <div className="relative mx-auto flex max-w-7xl flex-col items-start justify-between gap-6 px-4 py-12 sm:flex-row sm:items-center sm:px-6 lg:px-8">
                    <div>
                        <h2 className="font-display text-2xl font-bold sm:text-3xl">Ready to upgrade your setup?</h2>
                        <p className="mt-2 max-w-lg text-brand-100">
                            Browse hundreds of new and pre-owned devices. Secure checkout, tracked shipping, and support that actually replies.
                        </p>
                    </div>
                    <Link to="/products" className="btn-primary shrink-0 !bg-white !px-7 !py-3 !text-brand-700 hover:!bg-brand-50">
                        Start shopping
                    </Link>
                </div>
            </section>
        </div>
    );
}
