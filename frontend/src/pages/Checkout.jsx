import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import {
    placeOrder,
    registerCustomer,
    updateCustomer,
    getCustomerByEmail,
    createShippingAddress,
    createPaymentDetails,
} from '../services/api';

function formatPrice(price) {
    return new Intl.NumberFormat('en-ZA', {
        style: 'currency',
        currency: 'ZAR',
    }).format(Number(price) || 0);
}

const DELIVERY_THRESHOLD = 7500; // free delivery at/above this
const DELIVERY_FEE = 75;         // fee if below threshold (and not Plus)
const PLUS_DISCOUNT_RATE = 0.05; // 5% off for TechVault Plus

export default function Checkout() {
    const { items, subtotal, clearCart } = useCart();
    const { user } = useAuth();

    const [form, setForm] = useState({
        firstName: user?.firstName || '',
        lastName: user?.lastName || '',
        email: user?.email || '',
        phone: user?.phone || '',
        address: user?.address || '',
        city: '',
        province: '',
        zipcode: '',
        paymentMethod: 'Cash on delivery',
        cardNumber: '',
        cardHolderName: '',
        cardExpiry: '',
        cardCvv: '',
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(null);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    // —— Pricing ——
    const hasPlus = !!(user?.subscribed || user?.hasActiveSubscription);
    const discount = hasPlus ? subtotal * PLUS_DISCOUNT_RATE : 0;
    const afterDiscount = subtotal - discount;
    const deliveryFee =
        hasPlus || afterDiscount >= DELIVERY_THRESHOLD ? 0 : DELIVERY_FEE;
    const total = afterDiscount + deliveryFee;

    // —— Empty cart ——
    if (items.length === 0 && !success) {
        return (
            <div className="mx-auto max-w-7xl px-4 py-20 text-center">
                <p className="text-slate-500">Your cart is empty.</p>
                <Link to="/products" className="btn-primary mt-4 inline-flex">
                    Shop now
                </Link>
            </div>
        );
    }

    // —— Success ——
    if (success) {
        return (
            <div className="mx-auto max-w-lg px-4 py-20 text-center animate-scale-in">
                <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-emerald-100">
                    <svg
                        className="h-10 w-10 text-emerald-600"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                        strokeWidth={2}
                    >
                        <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                </div>
                <h2 className="font-display text-2xl font-bold text-slate-900">Order placed!</h2>
                <p className="mt-2 text-slate-500">
                    Order #{success.id} has been created successfully.
                </p>
                <p className="mt-1 text-sm text-slate-400">
                    Total: {formatPrice(success.totalAmount ?? total)}
                </p>
                {success.paymentMethod && (
                    <p className="mt-1 text-sm text-slate-400">
                        Payment: {success.paymentMethod}
                    </p>
                )}
                {success.deliveryFee === 0 && (
                    <p className="mt-1 text-sm text-emerald-600">Delivery: FREE</p>
                )}
                {success.deliveryFee > 0 && (
                    <p className="mt-1 text-sm text-slate-400">
                        Delivery: {formatPrice(success.deliveryFee)}
                    </p>
                )}
                <div className="mt-8 flex justify-center gap-3">
                    <Link to={`/orders/${success.id}`} className="btn-primary">
                        Track this order
                    </Link>
                    <Link to="/orders" className="btn-secondary">
                        My orders
                    </Link>
                    <Link to="/products" className="btn-secondary">
                        Continue shopping
                    </Link>
                </div>
            </div>
        );
    }

    // —— Submit ——
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            // Basic card validation when paying by card
            if (form.paymentMethod === 'Card') {
                const cleaned = (form.cardNumber || '').replace(/\s+/g, '');
                if (cleaned.length < 12) {
                    throw new Error('Please enter a valid card number.');
                }
                if (!form.cardHolderName.trim()) {
                    throw new Error('Please enter the cardholder name.');
                }
                if (!/^\d{2}\/\d{2}$/.test(form.cardExpiry.trim())) {
                    throw new Error('Expiry must be in MM/YY format.');
                }
                if (!/^\d{3,4}$/.test(form.cardCvv.trim())) {
                    throw new Error('Please enter a valid CVV.');
                }
            }

            const details = {
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                email: form.email.trim(),
                phone: form.phone.trim(),
                address: form.address.trim(),
            };

            // 1. Find or create customer
            let customer;
            try {
                const existing = await getCustomerByEmail(details.email);
                customer = await updateCustomer(existing.id, details);
            } catch (err) {
                if (err.response?.status === 404) {
                    customer = await registerCustomer(details);
                } else {
                    throw err;
                }
            }

            // 2. Shipping address
            try {
                await createShippingAddress({
                    addressLine1: details.address,
                    addressLine2: '',
                    city: form.city.trim() || 'N/A',
                    province: form.province.trim() || 'N/A',
                    zipcode: form.zipcode.trim() || '0000',
                    country: 'South Africa',
                });
            } catch (shipErr) {
                console.warn('Shipping address save failed (order still proceeds):', shipErr);
            }

            // 3. Place order
            const order = await placeOrder({
                customerId: customer.id,
                shippingAddress: [
                    details.address,
                    form.city.trim(),
                    form.province.trim(),
                    form.zipcode.trim(),
                ]
                    .filter(Boolean)
                    .join(', '),
                productIds: items.map((i) => i.product.id),
                quantities: items.map((i) => i.quantity),
            });

            // 4. Payment details
            const paymentMethod = form.paymentMethod || 'Cash on Delivery';
            try {
                const cleaned = (form.cardNumber || '').replace(/\s+/g, '');
                const last4 = cleaned.length >= 4 ? cleaned.slice(-4) : cleaned;
                const masked = last4 ? `**** **** **** ${last4}` : null;

                await createPaymentDetails({
                    paymentId: `PAY-${order.id}-${Date.now()}`,
                    paymentMethod,
                    transactionId: `TXN-ORDER-${order.id}`,
                    cardNumberMasked: paymentMethod === 'Card' ? masked : null,
                    cardLast4: paymentMethod === 'Card' ? last4 : null,
                    cardHolderName:
                        paymentMethod === 'Card' ? form.cardHolderName.trim() : null,
                    cardExpiry: paymentMethod === 'Card' ? form.cardExpiry.trim() : null,
                    amount: total,
                    status: 'COMPLETED',
                    orderId: order.id,
                });
            } catch (payErr) {
                console.warn('Payment details save failed (order still proceeds):', payErr);
            }

            clearCart();
            setSuccess({
                ...order,
                paymentMethod,
                deliveryFee,
                totalAmount: order.totalAmount ?? total,
            });
        } catch (err) {
            console.error(err);
            const msg =
                err.response?.data?.error ||
                err.response?.data?.message ||
                err.message ||
                'Failed to place order';
            setError(typeof msg === 'string' ? msg : 'Failed to place order. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 animate-fade-in">
            <h1 className="mb-8 font-display text-3xl font-bold text-slate-900">Checkout</h1>

            <form onSubmit={handleSubmit} className="grid gap-8 lg:grid-cols-3">
                <div className="space-y-6 lg:col-span-2">
                    {/* Your details */}
                    <div className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">
                            Your details
                        </h2>
                        <div className="mt-4 grid gap-4 sm:grid-cols-2">
                            <input
                                name="firstName"
                                value={form.firstName}
                                onChange={handleChange}
                                placeholder="First name"
                                className="input-field"
                                required
                            />
                            <input
                                name="lastName"
                                value={form.lastName}
                                onChange={handleChange}
                                placeholder="Last name"
                                className="input-field"
                                required
                            />
                            <input
                                name="email"
                                type="email"
                                value={form.email}
                                onChange={handleChange}
                                placeholder="Email"
                                className="input-field"
                                required
                            />
                            <input
                                name="phone"
                                value={form.phone}
                                onChange={handleChange}
                                placeholder="Phone (optional)"
                                className="input-field"
                            />
                        </div>
                    </div>

                    {/* Shipping address */}
                    <div className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">
                            Shipping address
                        </h2>
                        <div className="mt-4 space-y-4">
                            <input
                                name="address"
                                value={form.address}
                                onChange={handleChange}
                                placeholder="Street address"
                                className="input-field"
                                required
                            />
                            <div className="grid gap-4 sm:grid-cols-3">
                                <input
                                    name="city"
                                    value={form.city}
                                    onChange={handleChange}
                                    placeholder="City"
                                    className="input-field"
                                />
                                <input
                                    name="province"
                                    value={form.province}
                                    onChange={handleChange}
                                    placeholder="Province"
                                    className="input-field"
                                />
                                <input
                                    name="zipcode"
                                    value={form.zipcode}
                                    onChange={handleChange}
                                    placeholder="Postal code"
                                    className="input-field"
                                />
                            </div>
                        </div>
                    </div>

                    {/* Payment method + card fields */}
                    <div className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">
                            Payment method
                        </h2>
                        <select
                            name="paymentMethod"
                            value={form.paymentMethod}
                            onChange={handleChange}
                            className="input-field mt-4"
                        >
                            <option value="Cash on Delivery">Cash on Delivery</option>
                            <option value="EFT">EFT / Bank Transfer</option>
                            <option value="Card">Card</option>
                        </select>

                        {form.paymentMethod === 'Card' && (
                            <div className="mt-6 space-y-4 border-t border-slate-100 pt-6">
                                <p className="text-sm text-slate-500">
                                    Enter your card details. Simulated payment for this project
                                    (no real charge).
                                </p>

                                <div>
                                    <label className="mb-1 block text-xs font-medium text-slate-600">
                                        Cardholder name
                                    </label>
                                    <input
                                        name="cardHolderName"
                                        value={form.cardHolderName}
                                        onChange={handleChange}
                                        placeholder="Name on card"
                                        className="input-field"
                                        required={form.paymentMethod === 'Card'}
                                    />
                                </div>

                                <div>
                                    <label className="mb-1 block text-xs font-medium text-slate-600">
                                        Card number
                                    </label>
                                    <input
                                        name="cardNumber"
                                        value={form.cardNumber}
                                        onChange={handleChange}
                                        placeholder="1234 5678 9012 3456"
                                        className="input-field"
                                        inputMode="numeric"
                                        maxLength={19}
                                        required={form.paymentMethod === 'Card'}
                                    />
                                </div>

                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <label className="mb-1 block text-xs font-medium text-slate-600">
                                            Expiry (MM/YY)
                                        </label>
                                        <input
                                            name="cardExpiry"
                                            value={form.cardExpiry}
                                            onChange={handleChange}
                                            placeholder="12/28"
                                            className="input-field"
                                            maxLength={5}
                                            required={form.paymentMethod === 'Card'}
                                        />
                                    </div>
                                    <div>
                                        <label className="mb-1 block text-xs font-medium text-slate-600">
                                            CVV
                                        </label>
                                        <input
                                            name="cardCvv"
                                            value={form.cardCvv}
                                            onChange={handleChange}
                                            placeholder="123"
                                            className="input-field"
                                            inputMode="numeric"
                                            maxLength={4}
                                            required={form.paymentMethod === 'Card'}
                                        />
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Items table */}
                    <div className="card p-6">
                        <h2 className="font-display text-lg font-bold text-slate-900">
                            Order items
                        </h2>
                        <div className="mt-4 overflow-x-auto">
                            <table className="w-full text-left text-sm">
                                <thead>
                                <tr className="border-b border-slate-100 text-xs uppercase tracking-wide text-slate-400">
                                    <th className="pb-2 font-medium">Item</th>
                                    <th className="pb-2 text-center font-medium">Qty</th>
                                    <th className="pb-2 text-right font-medium">Price</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-50">
                                {items.map(({ product, quantity }) => (
                                    <tr key={product.id}>
                                        <td className="py-3 text-slate-700">{product.name}</td>
                                        <td className="py-3 text-center text-slate-500">
                                            {quantity}
                                        </td>
                                        <td className="py-3 text-right font-medium">
                                            {formatPrice(Number(product.price) * quantity)}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                {/* Summary sidebar */}
                <div className="card h-fit p-6">
                    <h2 className="font-display text-lg font-bold text-slate-900">Summary</h2>
                    <div className="mt-4 space-y-2 text-sm">
                        <div className="flex justify-between">
                            <span className="text-slate-500">Subtotal</span>
                            <span className="font-medium">{formatPrice(subtotal)}</span>
                        </div>

                        {hasPlus && discount > 0 && (
                            <div className="flex justify-between text-emerald-600">
                                <span>Plus discount (5%)</span>
                                <span className="font-medium">-{formatPrice(discount)}</span>
                            </div>
                        )}

                        <div className="flex justify-between">
                            <span className="text-slate-500">
                                Delivery
                                {hasPlus && (
                                    <span className="ml-1 text-xs text-emerald-600">(Plus · free)</span>
                                )}
                                {!hasPlus && afterDiscount >= DELIVERY_THRESHOLD && (
                                    <span className="ml-1 text-xs text-emerald-600">
                                        (free over R7 500)
                                    </span>
                                )}
                            </span>
                            <span className="font-medium">
                                {deliveryFee === 0 ? 'FREE' : formatPrice(deliveryFee)}
                            </span>
                        </div>

                        {!hasPlus && afterDiscount < DELIVERY_THRESHOLD && (
                            <p className="text-xs text-slate-400">
                                Add {formatPrice(DELIVERY_THRESHOLD - afterDiscount)} more for free
                                delivery, or join TechVault Plus (R50/mo) for unlimited free
                                deliveries.
                            </p>
                        )}

                        <div className="flex justify-between border-t border-slate-100 pt-3 text-base">
                            <span className="font-semibold">Total</span>
                            <span className="font-display font-bold">{formatPrice(total)}</span>
                        </div>
                    </div>

                    {error && (
                        <p className="mt-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
                            {error}
                        </p>
                    )}

                    <button type="submit" disabled={loading} className="btn-primary mt-6 w-full">
                        {loading ? 'Placing order…' : 'Place order'}
                    </button>

                    <p className="mt-3 text-center text-xs text-slate-400">
                        <Link to="/cart" className="text-brand-600 hover:text-brand-700">
                            ← Back to cart
                        </Link>
                    </p>
                </div>
            </form>
        </div>
    );
}