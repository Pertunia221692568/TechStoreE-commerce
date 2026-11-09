import { Link, NavLink, useNavigate, useLocation } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useState, useEffect, useRef } from 'react';
import Logo from './Logo';

export default function Navbar() {
    const { totalItems } = useCart();
    const { user, logout, isAuthenticated } = useAuth();
    const [search, setSearch] = useState('');
    const [mobileOpen, setMobileOpen] = useState(false);
    const [accountOpen, setAccountOpen] = useState(false);
    const isAdmin = user?.role === 'ADMIN';
    const navigate = useNavigate();
    const location = useLocation();
    const accountRef = useRef(null);

    useEffect(() => {
        setMobileOpen(false);
        setAccountOpen(false);
    }, [location.pathname]);

    // Search box: sync on /products, clear elsewhere
    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const q = params.get('search') || '';
        if (location.pathname === '/products') setSearch(q);
        else setSearch('');
    }, [location.pathname, location.search]);

    // Close account dropdown when clicking outside
    useEffect(() => {
        const onClick = (e) => {
            if (accountRef.current && !accountRef.current.contains(e.target)) {
                setAccountOpen(false);
            }
        };
        document.addEventListener('mousedown', onClick);
        return () => document.removeEventListener('mousedown', onClick);
    }, []);

    const handleSearch = (e) => {
        e.preventDefault();
        if (search.trim()) {
            navigate(`/products?search=${encodeURIComponent(search.trim())}`);
            setMobileOpen(false);
        }
    };

    const handleLogout = () => {
        logout();
        setAccountOpen(false);
        setMobileOpen(false);
        navigate('/');
    };

    const linkClass = ({ isActive }) =>
        `relative text-sm font-semibold transition-colors ${
            isActive
                ? 'text-brand-600 after:absolute after:-bottom-1 after:left-0 after:h-0.5 after:w-full after:rounded-full after:bg-brand-600'
                : 'text-slate-600 hover:text-brand-600'
        }`;

    return (
        <header className="sticky top-0 z-50 border-b border-slate-200/80 bg-white/95 shadow-sm backdrop-blur-md">
            {/* Top trust bar */}
            <div className="hidden border-b border-slate-100 bg-slate-900 text-xs text-slate-300 sm:block">
                <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-1.5 sm:px-6 lg:px-8">
                    <div className="flex items-center gap-4">
                        <span className="flex items-center gap-1.5">
                            <span className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
                            Free shipping over R999
                        </span>
                        <span className="hidden md:inline">·</span>
                        <span className="hidden md:inline">14-day easy returns</span>
                        <span className="hidden lg:inline">·</span>
                        <span className="hidden lg:inline">Genuine & certified pre-owned tech</span>
                    </div>
                    <div className="flex items-center gap-3">
                        <Link to="/shipping" className="transition hover:text-white">Shipping</Link>
                        <Link to="/help" className="transition hover:text-white">Help</Link>
                        <Link to="/contact" className="transition hover:text-white">Contact</Link>
                    </div>
                </div>
            </div>

            {/* Main bar – Takealot-style: logo | search | account | cart */}
            <div className="mx-auto flex max-w-7xl items-center gap-3 px-4 py-3 sm:gap-4 sm:px-6 lg:px-8">
                <Logo size="md" />

                {/* Desktop nav links */}
                <nav className="ml-2 hidden items-center gap-5 md:flex">
                    <NavLink to="/" className={linkClass} end>Home</NavLink>
                    <NavLink to="/products" className={linkClass}>Shop</NavLink>
                    <NavLink to="/orders" className={linkClass}>Orders</NavLink>
                    {isAdmin && (
                        <NavLink to="/admin" className={linkClass}>Admin</NavLink>
                    )}
                </nav>

                {/* Search – grows in the middle */}
                <form onSubmit={handleSearch} className="ml-auto hidden min-w-0 max-w-md flex-1 lg:block">
                    <div className="relative">
                        <input
                            type="search"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder="Search for products, brands..."
                            className="input-field !py-2.5 !pr-11 text-sm"
                            aria-label="Search products"
                        />
                        <button
                            type="submit"
                            className="absolute right-2 top-1/2 -translate-y-1/2 rounded-lg bg-brand-600 p-1.5 text-white hover:bg-brand-700"
                            aria-label="Search"
                        >
                            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
                            </svg>
                        </button>
                    </div>
                </form>

                {/* Right: Account + Cart */}
                <div className="flex items-center gap-2 sm:gap-3">
                    {/* Account – Takealot style */}
                    <div className="relative hidden sm:block" ref={accountRef}>
                        {isAuthenticated ? (
                            <>
                                <button
                                    type="button"
                                    onClick={() => setAccountOpen((o) => !o)}
                                    className="flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2 text-sm font-medium text-slate-700 transition hover:border-brand-300 hover:bg-slate-50"
                                    aria-expanded={accountOpen}
                                >
                                    <svg className="h-5 w-5 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                    </svg>
                                    <span className="max-w-[7rem] truncate">
                                        {user?.firstName || 'My account'}
                                    </span>
                                    <svg className={`h-4 w-4 text-slate-400 transition ${accountOpen ? 'rotate-180' : ''}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                                    </svg>
                                </button>

                                {accountOpen && (
                                    <div className="absolute right-0 mt-2 w-56 overflow-hidden rounded-xl border border-slate-200 bg-white py-1 shadow-lg">
                                        <div className="border-b border-slate-100 px-4 py-3">
                                            <p className="text-sm font-semibold text-slate-900">
                                                {user?.firstName} {user?.lastName}
                                            </p>
                                            <p className="truncate text-xs text-slate-500">{user?.email}</p>
                                        </div>
                                        {isAdmin && (
                                            <Link
                                                to="/admin"
                                                onClick={() => setAccountOpen(false)}
                                                className="flex items-center gap-2 px-4 py-2.5 text-sm font-semibold text-brand-600 hover:bg-slate-50"
                                            >
                                                Admin dashboard
                                            </Link>
                                        )}
                                        <Link
                                            to="/profile"
                                            onClick={() => setAccountOpen(false)}
                                            className="flex items-center gap-2 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50"
                                        >
                                            <svg className="h-4 w-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                                <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                            </svg>
                                            Manage profile
                                        </Link>
                                        <Link
                                            to="/orders"
                                            onClick={() => setAccountOpen(false)}
                                            className="flex items-center gap-2 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50"
                                        >
                                            <svg className="h-4 w-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                                <path strokeLinecap="round" strokeLinejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                                            </svg>
                                            My orders
                                        </Link>
                                        <button
                                            type="button"
                                            onClick={handleLogout}
                                            className="flex w-full items-center gap-2 border-t border-slate-100 px-4 py-2.5 text-left text-sm font-medium text-red-600 hover:bg-red-50"
                                        >
                                            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                                <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                            </svg>
                                            Log out
                                        </button>
                                    </div>
                                )}
                            </>
                        ) : (
                            <div className="flex items-center gap-2">
                                <Link
                                    to="/login"
                                    className="rounded-xl border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-700 transition hover:border-brand-300 hover:bg-slate-50"
                                >
                                    Sign in
                                </Link>
                                <Link
                                    to="/login?mode=register"
                                    className="rounded-xl bg-brand-600 px-3 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-700"
                                >
                                    Register
                                </Link>
                            </div>
                        )}
                    </div>

                    {/* Cart */}
                    <Link
                        to="/cart"
                        className="relative flex h-10 items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 text-slate-700 transition hover:border-brand-300 hover:text-brand-600"
                        aria-label={`Cart with ${totalItems} items`}
                    >
                        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        <span className="hidden text-sm font-semibold sm:inline">Cart</span>
                        {totalItems > 0 && (
                            <span className="absolute -right-1.5 -top-1.5 flex h-5 min-w-[1.25rem] items-center justify-center rounded-full bg-accent-500 px-1 text-[10px] font-bold text-white shadow">
                                {totalItems > 99 ? '99+' : totalItems}
                            </span>
                        )}
                    </Link>

                    {/* Mobile menu button */}
                    <button
                        onClick={() => setMobileOpen(!mobileOpen)}
                        className="flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 md:hidden"
                        aria-label="Toggle menu"
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

            {/* Mobile drawer */}
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
                    <div className="flex flex-col gap-1">
                        <NavLink to="/" className={linkClass} end>Home</NavLink>
                        <NavLink to="/products" className={linkClass}>Shop</NavLink>
                        <NavLink to="/orders" className={linkClass}>Orders</NavLink>

                        <div className="my-2 border-t border-slate-100" />

                        {isAuthenticated ? (
                            <>
                                <p className="px-0 py-1 text-xs font-semibold uppercase tracking-wide text-slate-400">
                                    {user?.firstName || 'Account'}
                                </p>
                                {isAdmin && <NavLink to="/admin" className={linkClass}>Admin dashboard</NavLink>}
                                <NavLink to="/profile" className={linkClass}>Manage profile</NavLink>
                                <NavLink to="/orders" className={linkClass}>My orders</NavLink>
                                <button
                                    type="button"
                                    onClick={handleLogout}
                                    className="py-2 text-left text-sm font-semibold text-red-600"
                                >
                                    Log out
                                </button>
                            </>
                        ) : (
                            <>
                                <Link
                                    to="/login"
                                    className="rounded-xl border border-slate-200 px-3 py-2.5 text-center text-sm font-semibold text-slate-700"
                                >
                                    Sign in
                                </Link>
                                <Link
                                    to="/login?mode=register"
                                    className="mt-2 rounded-xl bg-brand-600 px-3 py-2.5 text-center text-sm font-semibold text-white"
                                >
                                    Register
                                </Link>
                            </>
                        )}
                    </div>
                </div>
            )}
        </header>
    );
}