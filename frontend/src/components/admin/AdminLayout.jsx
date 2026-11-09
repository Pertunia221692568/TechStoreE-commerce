import { useState } from 'react';
import { NavLink, Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const NAV = [
    { to: '/admin', label: 'Dashboard', end: true },
    { to: '/admin/products', label: 'Products' },
    { to: '/admin/orders', label: 'Orders' },
];

export default function AdminLayout() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/admin/login');
    };

    const linkClass = ({ isActive }) =>
        `block rounded-lg px-3 py-2 text-sm font-medium transition ${
            isActive ? 'bg-brand-600 text-white' : 'text-slate-300 hover:bg-slate-800 hover:text-white'
        }`;

    return (
        <div className="flex min-h-screen bg-slate-100">
            {/* Sidebar */}
            <aside
                className={`${open ? 'block' : 'hidden'} fixed inset-y-0 left-0 z-40 w-60 flex-col bg-slate-900 p-4 md:static md:flex`}
            >
                <div className="mb-6 px-2">
                    <p className="font-display text-lg font-bold text-white">TechVault</p>
                    <p className="text-xs uppercase tracking-wide text-slate-400">Admin panel</p>
                </div>
                <nav className="flex-1 space-y-1" onClick={() => setOpen(false)}>
                    {NAV.map((n) => (
                        <NavLink key={n.to} to={n.to} end={n.end} className={linkClass}>
                            {n.label}
                        </NavLink>
                    ))}
                </nav>
                <Link to="/" className="mt-4 block rounded-lg px-3 py-2 text-sm text-slate-400 hover:text-white">
                    View store →
                </Link>
            </aside>

            {/* Main */}
            <div className="flex min-w-0 flex-1 flex-col">
                <header className="flex items-center justify-between border-b border-slate-200 bg-white px-4 py-3">
                    <button
                        type="button"
                        onClick={() => setOpen((o) => !o)}
                        className="rounded-lg border border-slate-200 px-3 py-1.5 text-sm md:hidden"
                    >
                        Menu
                    </button>
                    <p className="hidden text-sm text-slate-500 md:block">
                        Signed in as <span className="font-semibold text-slate-800">{user?.email}</span>
                    </p>
                    <button
                        type="button"
                        onClick={handleLogout}
                        className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800"
                    >
                        Log out
                    </button>
                </header>
                <main className="flex-1 p-4 sm:p-6 lg:p-8">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
