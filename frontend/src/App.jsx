import { BrowserRouter, Routes, Route } from 'react-router-dom';
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
import Admin from './pages/Admin';
import Login from './pages/Login';
import Profile from './pages/Profile';
import HelpCentre from './pages/HelpCentre';
import ShippingInfo from './pages/ShippingInfo';
import Returns from './pages/Returns';
import Contact from './pages/Contact';

export default function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <CartProvider>
                    <div className="flex min-h-screen flex-col">
                        <Navbar />
                        <main className="flex-1">
                            <Routes>
                                <Route path="/" element={<Home />} />
                                <Route path="/products" element={<Products />} />
                                <Route path="/products/:id" element={<ProductDetail />} />
                                <Route path="/cart" element={<Cart />} />
                                <Route path="/checkout" element={<Checkout />} />
                                <Route path="/orders" element={<Orders />} />
                                <Route path="/login" element={<Login />} />
                                <Route path="/profile" element={<Profile />} />
                                <Route path="/admin" element={<Admin />} />
                                <Route path="/help" element={<HelpCentre />} />
                                <Route path="/shipping" element={<ShippingInfo />} />
                                <Route path="/returns" element={<Returns />} />
                                <Route path="/contact" element={<Contact />} />
                            </Routes>
                        </main>
                        <Footer />
                    </div>
                </CartProvider>
            </AuthProvider>
        </BrowserRouter>
    );
}