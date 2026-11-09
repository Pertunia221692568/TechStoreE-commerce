import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function AdminDashboard() {
    const { user } = useAuth();
    const cards = [
        { to: '/admin/products', title: 'Products', text: 'Add, edit and delete products.' },
        { to: '/admin/orders', title: 'Orders', text: 'View orders and update their status.' },
    ];

    return (
        <div className="animate-fade-in">
            <h1 className="font-display text-2xl font-bold text-slate-900">Welcome, {user?.firstName}</h1>
            <p className="mt-1 text-sm text-slate-500">Manage your TechVault store from here.</p>
            <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                {cards.map((c) => (
                    <Link key={c.to} to={c.to} className="card p-5 transition hover:shadow-md">
                        <h2 className="font-display text-lg font-bold text-slate-900">{c.title}</h2>
                        <p className="mt-1 text-sm text-slate-500">{c.text}</p>
                    </Link>
                ))}
            </div>
        </div>
    );
}
