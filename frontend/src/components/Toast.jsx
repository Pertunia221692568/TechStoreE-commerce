import { useEffect } from 'react';

export default function Toast({ message, type = 'success', onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 3000);
    return () => clearTimeout(t);
  }, [onClose]);

  const colors = {
    success: 'bg-emerald-600',
    error: 'bg-red-600',
    info: 'bg-brand-600',
  };

  return (
    <div className={`fixed bottom-6 right-6 z-[100] flex items-center gap-3 rounded-xl ${colors[type]} px-5 py-3 text-sm font-medium text-white shadow-lg animate-slide-up`}>
      {message}
      <button onClick={onClose} className="ml-2 opacity-80 hover:opacity-100">✕</button>
    </div>
  );
}
