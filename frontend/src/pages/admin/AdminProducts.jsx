import { useState } from 'react';
import { ProductsTab } from './AdminTabs';

export default function AdminProducts() {
    const [message, setMessage] = useState(null);
    const showMessage = (type, text) => {
        setMessage({ type, text });
        if (type === 'ok') setTimeout(() => setMessage(null), 3500);
    };
    return (
        <div>
            <h1 className="mb-4 font-display text-2xl font-bold text-slate-900">Products</h1>
            {message && (
                <div className={`mb-4 rounded-xl border px-4 py-3 text-sm font-medium ${
                    message.type === 'ok' ? 'border-emerald-200 bg-emerald-50 text-emerald-800' : 'border-red-200 bg-red-50 text-red-800'
                }`}>{message.text}</div>
            )}
            <ProductsTab showMessage={showMessage} />
        </div>
    );
}
