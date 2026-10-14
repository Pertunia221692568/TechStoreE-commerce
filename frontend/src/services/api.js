import axios from 'axios';

const api = axios.create({
    baseURL: '', // proxy handles /api -> localhost:8080
    headers: { 'Content-Type': 'application/json' },
});

// ── Products ──────────────────────────────────────────────────
export const getProducts = (params = {}) =>
    api.get('/api/products', { params }).then(r => r.data);

export const getProduct = (id) =>
    api.get(`/api/products/${id}`).then(r => r.data);

export const getInStockProducts = () =>
    api.get('/api/products/in-stock').then(r => r.data);

// ── Customers ─────────────────────────────────────────────────
export const registerCustomer = (data) =>
    api.post('/api/customers', data).then(r => r.data);

export const getCustomerByEmail = (email) =>
    api.get('/api/customers/by-email', { params: { email } }).then(r => r.data);

export const getCustomer = (id) =>
    api.get(`/api/customers/${id}`).then(r => r.data);

export const updateCustomer = (id, data) =>
    api.put(`/api/customers/${id}`, data).then(r => r.data);

// ── Orders ────────────────────────────────────────────────────
export const placeOrder = (payload) =>
    api.post('/api/orders', payload).then(r => r.data);

export const getOrdersByCustomer = (customerId) =>
    api.get(`/api/orders/customer/${customerId}`).then(r => r.data);

export const getOrder = (id) =>
    api.get(`/api/orders/${id}`).then(r => r.data);

export const cancelOrder = (id) =>
    api.post(`/api/orders/${id}/cancel`).then(r => r.data);

// ── Ratings ───────────────────────────────────────────────────
export const createRating = (rating) =>
    api.post('/api/ratings', rating).then(r => r.data);

export const getRatings = () =>
    api.get('/api/ratings').then(r => r.data);

// ── Login / Registration (simple) ─────────────────────────────
export const createLogin = (login) =>
    api.post('/api/logins', login).then(r => r.data);

export const createRegistration = (reg) =>
    api.post('/api/registrations', reg).then(r => r.data);

export default api;
