import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
import { CartProvider } from './context/CartContext';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import Home from './pages/Home';
import Products from './pages/Products';
import ProductDetail from './pages/ProductDetail';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import Orders from './pages/Orders';
import OrderDetail from './pages/OrderDetail';
import Login from './pages/Login';
import Profile from './pages/Profile';
import HelpCentre from './pages/HelpCentre';
import ShippingInfo from './pages/ShippingInfo';
import Returns from './pages/Returns';
import Contact from './pages/Contact';
import RequireAdmin from './components/admin/RequireAdmin';
import AdminLayout from './components/admin/AdminLayout';
import AdminLogin from './pages/admin/AdminLogin';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminProducts from './pages/admin/AdminProducts';
import AdminOrders from './pages/admin/AdminOrders';

// Customer-facing layout: store navbar + footer
function StoreLayout() {
    return (
        <div className="flex min-h-screen flex-col">
            <Navbar />
            <main className="flex-1">
                <Outlet />
            </main>
            <Footer />
        </div>
    );
}

export default function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <CartProvider>
                    <Routes>
                        {/* Customer site */}
                        <Route element={<StoreLayout />}>
                            <Route path="/" element={<Home />} />
                            <Route path="/products" element={<Products />} />
                            <Route path="/products/:id" element={<ProductDetail />} />
                            <Route path="/cart" element={<Cart />} />
                            <Route path="/checkout" element={<Checkout />} />
                            <Route path="/orders" element={<Orders />} />
                            <Route path="/orders/:id" element={<OrderDetail />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/profile" element={<Profile />} />
                            <Route path="/help" element={<HelpCentre />} />
                            <Route path="/shipping" element={<ShippingInfo />} />
                            <Route path="/returns" element={<Returns />} />
                            <Route path="/contact" element={<Contact />} />
                        </Route>

                        {/* Admin site: its own login and layout */}
                        <Route path="/admin/login" element={<AdminLogin />} />
                        <Route element={<RequireAdmin />}>
                            <Route element={<AdminLayout />}>
                                <Route path="/admin" element={<AdminDashboard />} />
                                <Route path="/admin/products" element={<AdminProducts />} />
                                <Route path="/admin/orders" element={<AdminOrders />} />
                            </Route>
                        </Route>
                    </Routes>
                </CartProvider>
            </AuthProvider>
        </BrowserRouter>
    );
}
