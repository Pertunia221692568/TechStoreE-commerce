import { Link } from 'react-router-dom';
import Logo from './Logo';

export default function Footer() {
    return (
        <footer className="mt-auto border-t border-slate-200 bg-surface-900 text-slate-300">
            <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
                <div className="grid gap-8 md:grid-cols-4">
                    <div className="md:col-span-1">
                        <div className="[&_span]:!text-white [&_.text-slate-900]:!text-white [&_.text-brand-600]:!text-brand-400">
                            <Logo size="md" to="/" />
                        </div>
                        <p className="mt-3 text-sm leading-relaxed text-slate-400">
                            Premium tech e-commerce store. New & certified pre-owned laptops, desktops, phones, monitors and accessories — delivered across South Africa.
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
                            <li><Link to="/help" className="hover:text-accent-400 transition">Help Centre</Link></li>
                            <li><Link to="/shipping" className="hover:text-accent-400 transition">Shipping Info</Link></li>
                            <li><Link to="/returns" className="hover:text-accent-400 transition">Returns</Link></li>
                            <li><Link to="/contact" className="hover:text-accent-400 transition">Contact Us</Link></li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="mb-3 text-sm font-semibold uppercase tracking-wider text-white">Account</h4>
                        <ul className="space-y-2 text-sm">
                            <li><Link to="/login" className="hover:text-accent-400 transition">Login</Link></li>
                            <li><Link to="/orders" className="hover:text-accent-400 transition">My Orders</Link></li>
                            <li><Link to="/cart" className="hover:text-accent-400 transition">Cart</Link></li>
                        </ul>
                    </div>
                </div>

                <div className="mt-10 flex flex-col items-center justify-between gap-4 border-t border-slate-700 pt-8 sm:flex-row">
                    <p className="text-xs text-slate-500">
                        © {new Date().getFullYear()} TechVault  · ADP372S
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
