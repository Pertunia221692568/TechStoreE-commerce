import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useState } from 'react';

export default function Navbar() {
    const { totalItems } = useCart();
    const [search, setSearch] = useState('');
    const [mobileOpen, setMobileOpen] = useState(false);
    const navigate = useNavigate();

    const handleSearch = (e) => {
        e.preventDefault();
        if (search.trim()) {
            navigate(`/products?search=${encodeURIComponent(search.trim())}`);
            setMobileOpen(false);
        }
    };

    const linkClass = ({ isActive }) =>
        `text-sm font-medium transition-colors ${
            isActive ? 'text-brand-600' : 'text-slate-600 hover:text-brand-600'
        }`;

    return (
        <header className="sticky top-0 z-50 border-b border-slate-200/80 bg-white/90 backdrop-blur-md">
            <div className="mx-auto flex max-w-7xl items-center gap-4 px-4 py-3 sm:px-6 lg:px-8">
                {/* Logo */}
                <Link to="/" className="flex shrink-0 items-center gap-2">
                    <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-brand-600 to-brand-800 shadow-glow">
                        <svg className="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                        </svg>
                    </div>
                    <span className="font-display text-xl font-bold tracking-tight text-slate-900">
            Tech<span className="text-brand-600">Vault</span>
          </span>
                </Link>

                {/* Search – desktop */}
                <form onSubmit={handleSearch} className="mx-4 hidden flex-1 max-w-xl md:flex">
                    <div className="relative w-full">
                        <input
                            type="search"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder="Search laptops, desktops, phones, accessories..."
                            className="input-field pr-12"
                        />
                        <button
                            type="submit"
                            className="absolute right-2 top-1/2 -translate-y-1/2 rounded-lg bg-brand-600 p-1.5 text-white hover:bg-brand-700"
                        >
                            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </button>
                    </div>
                </form>

                {/* Nav links */}
                <nav className="ml-auto hidden items-center gap-6 md:flex">
                    <NavLink to="/" className={linkClass} end>Home</NavLink>
                    <NavLink to="/products" className={linkClass}>Shop</NavLink>
                    <NavLink to="/orders" className={linkClass}>Orders</NavLink>
                </nav>

                {/* Actions */}
                <div className="flex items-center gap-2">
                    <Link
                        to="/cart"
                        className="relative flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 bg-white text-slate-700 transition hover:border-brand-300 hover:text-brand-600"
                    >
                        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        {totalItems > 0 && (
                            <span className="absolute -right-1.5 -top-1.5 flex h-5 min-w-[1.25rem] items-center justify-center rounded-full bg-accent-500 px-1 text-[10px] font-bold text-white shadow">
                {totalItems > 99 ? '99+' : totalItems}
              </span>
                        )}
                    </Link>

                    {/* Mobile menu toggle */}
                    <button
                        onClick={() => setMobileOpen(!mobileOpen)}
                        className="flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 md:hidden"
                    >
                        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            {mobileOpen ? (
                                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                            ) : (
                                <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
                            )}
                        </svg>
                    </button>
                </div>
            </div>

            {/* Mobile panel */}
            {mobileOpen && (
                <div className="border-t border-slate-100 bg-white px-4 py-4 md:hidden animate-slide-up">
                    <form onSubmit={handleSearch} className="mb-4">
                        <input
                            type="search"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder="Search products..."
                            className="input-field"
                        />
                    </form>
                    <div className="flex flex-col gap-3">
                        <NavLink to="/" onClick={() => setMobileOpen(false)} className={linkClass} end>Home</NavLink>
                        <NavLink to="/products" onClick={() => setMobileOpen(false)} className={linkClass}>Shop</NavLink>
                        <NavLink to="/orders" onClick={() => setMobileOpen(false)} className={linkClass}>Orders</NavLink>
                    </div>
                </div>
            )}
        </header>
    );
}
