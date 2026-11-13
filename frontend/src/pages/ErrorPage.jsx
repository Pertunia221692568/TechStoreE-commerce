import { Link } from 'react-router-dom';

// Simple full-width error screen used for 403 (access denied) and 404 (not found)
export default function ErrorPage({ code, title, message, showAdminLogin = false }) {
    return (
        <div className="flex min-h-[70vh] flex-col items-center justify-center px-4 text-center">
            <p className="font-display text-6xl font-bold text-brand-600">{code}</p>
            <h1 className="mt-4 font-display text-2xl font-bold text-slate-900">{title}</h1>
            <p className="mt-2 max-w-md text-slate-500">{message}</p>
            <div className="mt-6 flex flex-wrap justify-center gap-3">
                <Link
                    to="/"
                    className="rounded-xl bg-brand-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-brand-700"
                >
                    Back to the shop
                </Link>
                {showAdminLogin && (
                    <Link
                        to="/admin/login"
                        className="rounded-xl border border-slate-300 px-5 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50"
                    >
                        Admin sign in
                    </Link>
                )}
            </div>
        </div>
    );
}
