import axios from 'axios';

const api = axios.create({
    baseURL: '',
    headers: { 'Content-Type': 'application/json' },
});

// Products
export const getProducts = (params = {}) =>
    api.get('/api/products', { params }).then(r => r.data);

export const getProduct = (id) =>
    api.get(`/api/products/${id}`).then(r => r.data);

export const getInStockProducts = () =>
    api.get('/api/products/in-stock').then(r => r.data);

export const createProduct = (data) =>
    api.post('/api/products', data).then(r => r.data);

export const updateProduct = (id, data) =>
    api.put(`/api/products/${id}`, data).then(r => r.data);

export const deleteProduct = (id) =>
    api.delete(`/api/products/${id}`).then(r => r.data);

export const uploadProductImage = (file) => {
    const form = new FormData();
    form.append('file', file);
    return api
        .post('/api/products/upload', form, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(r => r.data);
};

// Cart
export const getCart = (cartId) =>
    api.get(`/shopping-cart/${cartId}`).then(r => r.data);

export const addCartItem = (cartId, productId, quantity) =>
    api.post(`/shopping-cart/${cartId}/items`, { productId, quantity }).then(r => r.data);

export const updateCartItem = (cartId, productId, quantity) =>
    api.put(`/shopping-cart/${cartId}/items/${productId}`, { quantity }).then(r => r.data);

export const removeCartItem = (cartId, productId) =>
    api.delete(`/shopping-cart/${cartId}/items/${productId}`).then(r => r.data);

export const clearCartItems = (cartId) =>
    api.delete(`/shopping-cart/${cartId}`).then(r => r.data);

// Customers
export const registerCustomer = (data) =>
    api.post('/api/customers', data).then(r => r.data);

export const getCustomerByEmail = (email) =>
    api.get('/api/customers/by-email', { params: { email } }).then(r => r.data);

export const getCustomer = (id) =>
    api.get(`/api/customers/${id}`).then(r => r.data);

export const updateCustomer = (id, data) =>
    api.put(`/api/customers/${id}`, data).then(r => r.data);
export const deleteCustomer = (id) =>
    api.delete(`/api/customers/${id}`);
// Orders
export const placeOrder = (payload) =>
    api.post('/api/orders', payload).then(r => r.data);

export const getOrders = (params = {}) =>
    api.get('/api/orders', { params }).then(r => r.data);

export const getOrdersByCustomer = (customerId) =>
    api.get(`/api/orders/customer/${customerId}`).then(r => r.data);

export const getOrder = (id) =>
    api.get(`/api/orders/${id}`).then(r => r.data);

export const cancelOrder = (id) =>
    api.post(`/api/orders/${id}/cancel`).then(r => r.data);

export const updateOrderStatus = (id, status) =>
    api.patch(`/api/orders/${id}/status`, { status }).then(r => r.data);

// Ratings
export const createRating = (rating) =>
    api.post('/api/ratings', rating).then(r => r.data);

export const getRatings = () =>
    api.get('/api/ratings').then(r => r.data);

export const getRatingsByProduct = (productId) =>
    api.get(`/api/ratings/product/${productId}`).then(r => r.data);

// Shipping & payment
export const createShippingAddress = (address) =>
    api.post('/api/shipping-addresses', address).then(r => r.data);

export const createPaymentDetails = (payment) =>
    api.post('/payment-details/create', payment).then(r => r.data);

export const createLogin = (login) =>
    api.post('/api/logins', login).then(r => r.data);

export const createRegistration = (reg) =>
    api.post('/api/registrations', reg).then(r => r.data);

export default api;
