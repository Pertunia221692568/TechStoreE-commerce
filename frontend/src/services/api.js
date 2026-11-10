import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: { 'Content-Type': 'application/json' },
});

// ── Attach JWT token to every request ────────────────────────
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('techvault_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// ── Handle 401 / 403 ─────────────────────────────────────────
api.interceptors.response.use(
    (response) => response,
    (error) => {
        return Promise.reject(error);
    }
);

// ── AUTH ─────────────────────────────────────────────────────
export const login = (email, password) =>
    api.post('/api/auth/login', { email, password }).then((r) => r.data);

export const register = (data) =>
    api.post('/api/auth/register', data).then((r) => r.data);

export const getMe = () =>
    api.get('/api/auth/me').then((r) => r.data);

// ── Products ─────────────────────────────────────────────────
export const getProducts = (params = {}) =>
    api.get('/api/products', { params }).then((r) => r.data);

export const getProduct = (id) =>
    api.get(`/api/products/${id}`).then((r) => r.data);

export const getInStockProducts = () =>
    api.get('/api/products/in-stock').then((r) => r.data);

export const createProduct = (data) =>
    api.post('/api/products', data).then((r) => r.data);

export const updateProduct = (id, data) =>
    api.put(`/api/products/${id}`, data).then((r) => r.data);

export const deleteProduct = (id) =>
    api.delete(`/api/products/${id}`).then((r) => r.data);

export const uploadProductImage = (file) => {
    const form = new FormData();
    form.append('file', file);
    return api
        .post('/api/products/upload', form, {
            headers: { 'Content-Type': 'multipart/form-data' },
        })
        .then((r) => r.data);
};

// ── Cart ─────────────────────────────────────────────────────
export const getCart = (cartId) =>
    api.get(`/shopping-cart/${cartId}`).then((r) => r.data);

export const addCartItem = (cartId, productId, quantity) =>
    api
        .post(`/shopping-cart/${cartId}/items`, { productId, quantity })
        .then((r) => r.data);

export const updateCartItem = (cartId, productId, quantity) =>
    api
        .put(`/shopping-cart/${cartId}/items/${productId}`, { quantity })
        .then((r) => r.data);

export const removeCartItem = (cartId, productId) =>
    api.delete(`/shopping-cart/${cartId}/items/${productId}`).then((r) => r.data);

export const clearCartItems = (cartId) =>
    api.delete(`/shopping-cart/${cartId}`).then((r) => r.data);

// ── Customers ────────────────────────────────────────────────
export const registerCustomer = (data) =>
    api.post('/api/customers', data).then((r) => r.data);

export const getCustomerByEmail = (email) =>
    api.get('/api/customers/by-email', { params: { email } }).then((r) => r.data);

export const getCustomer = (id) =>
    api.get(`/api/customers/${id}`).then((r) => r.data);

export const updateCustomer = (id, data) =>
    api.put(`/api/customers/${id}`, data).then((r) => r.data);

export const deleteCustomer = (id) =>
    api.delete(`/api/customers/${id}`).then((r) => r.data);

// ── Orders ───────────────────────────────────────────────────
export const placeOrder = (data) =>
    api.post('/api/orders', data).then((r) => r.data);

export const createOrder = (data) =>
    api.post('/api/orders', data).then((r) => r.data);

export const getOrders = () =>
    api.get('/api/orders').then((r) => r.data);

export const getOrdersByCustomer = (customerId) =>
    api.get(`/api/orders/customer/${customerId}`).then((r) => r.data);

export const getOrdersByEmail = (email) =>
    api.get('/api/orders', { params: { email } }).then((r) => r.data);

export const getOrder = (id) =>
    api.get(`/api/orders/${id}`).then((r) => r.data);

export const getOrderTracking = (id) =>
    api.get(`/api/orders/${id}/tracking`).then((r) => r.data);

export const cancelOrder = (id) =>
    api.post(`/api/orders/${id}/cancel`).then((r) => r.data);

export const updateOrderStatus = (id, status) =>
    api.patch(`/api/orders/${id}/status`, { status }).then((r) => r.data);

// ── Ratings ──────────────────────────────────────────────────
export const createRating = (data) =>
    api.post('/api/ratings', data).then((r) => r.data);

export const getRatingsByProduct = (productId) =>
    api.get(`/api/ratings/product/${productId}`).then((r) => r.data);

export const getRatings = () =>
    api.get('/api/ratings').then((r) => r.data);

// ── Shipping & Payment ───────────────────────────────────────
export const createShippingAddress = (data) =>
    api.post('/api/shipping-addresses', data).then((r) => r.data);

export const createPaymentDetails = (data) =>
    api.post('/payment-details/create', data).then((r) => r.data);

// ── Legacy (still used by some pages) ────────────────────────
export const createRegistration = (data) =>
    api.post('/api/registrations', data).then((r) => r.data);

export const createLogin = (data) =>
    api.post('/api/logins', data).then((r) => r.data);

export const getRegistrations = () =>
    api.get('/api/registrations').then((r) => r.data);

export default api;