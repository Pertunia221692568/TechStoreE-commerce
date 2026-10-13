import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getProducts } from '../services/api';
import ProductCard from '../components/ProductCard';
import LoadingSpinner from '../components/LoadingSpinner';

const CATEGORIES = ['All', 'Laptop', 'Phone', 'Monitor', 'Keyboard', 'Mouse', 'Accessory', 'General'];

export default function Products() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const search = searchParams.get('search') || '';
  const category = searchParams.get('category') || 'All';

  useEffect(() => {
    setLoading(true);
    setError(null);
    const params = {};
    if (search) params.search = search;
    if (category && category !== 'All') params.category = category;

    getProducts(params)
      .then((data) => setProducts(Array.isArray(data) ? data : []))
      .catch((err) => {
        console.error(err);
        setError('Could not load products. Is the Spring Boot backend running on port 8080?');
        setProducts([]);
      })
      .finally(() => setLoading(false));
  }, [search, category]);

  const setCategory = (cat) => {
    const next = new URLSearchParams(searchParams);
    if (cat === 'All') next.delete('category');
    else next.set('category', cat);
    setSearchParams(next);
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8 animate-fade-in">
      <div className="mb-8">
        <h1 className="font-display text-3xl font-bold text-slate-900">Shop</h1>
        <p className="mt-1 text-sm text-slate-500">
          {search ? `Results for "${search}"` : 'Browse our full catalogue'}
        </p>
      </div>

      <div className="mb-8 flex flex-wrap gap-2">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => setCategory(cat)}
            className={`rounded-full px-4 py-1.5 text-sm font-medium transition ${
              (category === cat || (cat === 'All' && !category))
                ? 'bg-brand-600 text-white shadow-sm'
                : 'bg-white text-slate-600 border border-slate-200 hover:border-brand-300 hover:text-brand-600'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <div className="rounded-2xl border border-amber-200 bg-amber-50 p-8 text-center">
          <p className="font-medium text-amber-800">{error}</p>
          <p className="mt-2 text-sm text-amber-600">
            Start the backend with <code className="rounded bg-amber-100 px-1">./mvnw spring-boot:run</code>
          </p>
        </div>
      ) : products.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-200 py-16 text-center">
          <p className="text-slate-500">No products match your filters.</p>
        </div>
      ) : (
        <>
          <p className="mb-4 text-sm text-slate-500">{products.length} product{products.length !== 1 ? 's' : ''}</p>
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {products.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </>
      )}
    </div>
  );
}
