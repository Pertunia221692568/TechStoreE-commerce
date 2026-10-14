import { Link } from 'react-router-dom';

export default function Footer() {
    return (
        <footer className="mt-auto border-t border-slate-200 bg-surface-900 text-slate-300">
            <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
                <div className="grid gap-8 md:grid-cols-4">
                    <div className="md:col-span-1">
                        <div className="flex items-center gap-2">
                            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand-600">
                                <svg className="h-4 w-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.2}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                                </svg>
                            </div>
                            <span className="font-display text-lg font-bold text-white">TechVault</span>
                        </div>
                        <p className="mt-3 text-sm leading-relaxed text-slate-400">
                            Premium tech products delivered to your door. New & second-hand laptops, desktops, phones, accessories & more.
                        </p>
                    </div>

                    <div>
                        <h4 className="mb-3 text-sm font-semibold uppercase tracking-wider text-white">Shop</h4>
                        <ul className="space-y-2 text-sm">
                            <li><Link to="/products" className="hover:text-accent-400 transition">All Products</Link></li>
                            <li><Link to="/products?category=Laptop" className="hover:text-accent-400 transition">Laptops</Link></li>
                            <li><Link to="/products?category=Desktop" className="hover:text-accent-400 transition">Desktops</Link></li>
                            <li><Link to="/products?category=Phone" className="hover:text-accent-400 transition">Phones</Link></li>
                            <li><Link to="/products?category=Accessory" className="hover:text-accent-400 transition">Accessories</Link></li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="mb-3 text-sm font-semibold uppercase tracking-wider text-white">Support</h4>
                        <ul className="space-y-2 text-sm">
                            <li><a href="#" className="hover:text-accent-400 transition">Help Centre</a></li>
                            <li><a href="#" className="hover:text-accent-400 transition">Shipping Info</a></li>
                            <li><a href="#" className="hover:text-accent-400 transition">Returns</a></li>
                            <li><a href="#" className="hover:text-accent-400 transition">Contact Us</a></li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="mb-3 text-sm font-semibold uppercase tracking-wider text-white">Account</h4>
                        <ul className="space-y-2 text-sm">
                            <li><Link to="/orders" className="hover:text-accent-400 transition">My Orders</Link></li>
                            <li><Link to="/cart" className="hover:text-accent-400 transition">Cart</Link></li>
                        </ul>
                    </div>
                </div>

                <div className="mt-10 flex flex-col items-center justify-between gap-4 border-t border-slate-700 pt-8 sm:flex-row">
                    <p className="text-xs text-slate-500">
                        © {new Date().getFullYear()} TechVault  · Cape Peninsula University of Technology
                    </p>
                    <div className="flex gap-4 text-xs text-slate-500">
                        <span>Secure Checkout</span>
                        <span>·</span>
                        <span>Fast Delivery</span>
                        <span>·</span>
                        <span>Genuine Products</span>
                    </div>
                </div>
            </div>
        </footer>
    );
}
