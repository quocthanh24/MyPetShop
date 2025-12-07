import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.log('API Error Interceptor:', error.response?.status, error.response?.data);
    
    if (error.response?.status === 401) {
      // Token expired or invalid
      console.log('401 Unauthorized - Clearing tokens and redirecting');
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('userRole');
      // Only redirect if not already on login page
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  login: (credentials) => api.post('/login', credentials),
  register: (userData) => api.post('/register', userData),
  refreshToken: (refreshToken) => api.post('/refresh', { refreshToken }),
  requestOtp: (data) => api.post('/request-otp', data),
  registerWithOtp: (data) => api.post('/register-with-otp', data),
  requestOtpResetPassword: (data) => api.post('/request-otp/reset-password', data),
  verifyOtp: (data) => api.post('/verify-otp', data),
  resetPassword: (data) => api.post('/reset-password', data),
};

// Product APIs (for employees)
export const productAPI = {
  getAll: (page = 0, size = 10) => 
    api.get('/employees/products', { params: { page, size } }),
  search: (keyword, page = 0, size = 10) => 
    api.get('/employees/products/search', { params: { keyword, page, size } }),
  getByCategory: (categoryName, page = 0, size = 10) => 
    api.get('/employees/products/get-by-category', { params: { categoryName, page, size } }),
  getById: (id) => api.get(`/employees/products/${id}`),
};

// Customer Product APIs
export const customerProductAPI = {
  getAll: (page = 0, size = 10) => 
    api.get('/customers/products', { params: { page, size } }),
  search: (keyword, page = 0, size = 10) => 
    api.get('/customers/products/search', { params: { keyword, page, size } }),
  getByCategory: (categoryName, page = 0, size = 10) => 
    api.get('/customers/products/get-by-category', { params: { categoryName, page, size } }),
};

// Employee Product Management APIs
export const employeeProductAPI = {
  getAll: (page = 0, size = 10) => 
    api.get('/employees/products', { params: { page, size } }),
  search: (keyword, page = 0, size = 10) => 
    api.get('/employees/products/search', { params: { keyword, page, size } }),
  getByCategory: (categoryName, page = 0, size = 10) => 
    api.get('/employees/products/get-by-category', { params: { categoryName, page, size } }),
  create: (formData) => {
    // Create a new axios instance for form-data
    const formDataInstance = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    const token = localStorage.getItem('accessToken');
    if (token) {
      formDataInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    return formDataInstance.post('/employees/products/create-with-images', formData);
  },
  update: (productId, formData) => {
    const formDataInstance = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    const token = localStorage.getItem('accessToken');
    if (token) {
      formDataInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    return formDataInstance.put(`/employees/products/${productId}`, formData);
  },
  delete: (productId) => api.delete(`/employees/products/${productId}`),
};

// Category APIs
export const categoryAPI = {
  getAll: (page = 0, size = 10) => 
    api.get('/employees/categories', { params: { page, size } }),
  create: (categoryData) => 
    api.post('/employees/categories/create', categoryData),
  update: (id, categoryData) => 
    api.patch(`/employees/categories/${id}`, categoryData),
};

// Pet APIs
export const petAPI = {
  getByCode: (code) => api.get(`/employees/pets/${code}`),
  create: (petData) => api.post('/employees/pets/create-pet', petData),
};

// Cart APIs
export const cartAPI = {
  addToCart: (data) => api.post('/customers/add-to-cart', data),
  updateCartItem: (cartItemId, quantity) => 
    api.put(`/customers/cart/items/${cartItemId}`, null, { params: { quantity } }),
  getCartItems: (userId) => 
    api.get('/customers/get-products-from-cart', { params: { userId } }),
  deleteCartItem: (cartItemId) => 
    api.delete(`/customers/cart/items/${cartItemId}`),
};

// Order APIs
export const orderAPI = {
  placeOrder: (userId, orderData) => 
    api.post('/customers/orders', orderData, { params: { userId } }),
  cancelOrder: (orderNumber) => 
    api.post('/customers/orders/cancel', null, { params: { orderNumber } }),
  buyNow: (orderData) => 
    api.post('/customers/orders/buy-now', orderData),
  trackingOrders: (userId, page = 0, size = 10) => 
    api.get('/customers/orders/tracking', { params: { userId, page, size } }),
};

// Payment APIs
export const paymentAPI = {
  createMomoPayment: (orderId, amount) =>
    api.post('/payment/momo/create', { orderId, amount: amount.toString() }),
};

// Rating APIs
export const ratingAPI = {
  rateProduct: (productId, ratingData) => 
    api.post(`/customers/rate-product/${productId}`, ratingData),
};

// Medical Record APIs
export const medicalRecordAPI = {
  getByPhoneNumber: (phoneNumber) => 
    api.get('/employees/med-records/get-by-phone-number', { params: { phoneNumber } }),
  create: (data) => api.post('/employees/med-records/create-medrecord', data),
  getDetails: (medicalRecordId, page = 0) => 
    api.get(`/employees/med-records/details/${medicalRecordId}`, { params: { page } }),
  updateDetail: (medicalRecordId, detailData) => 
    api.put(`/employees/med-records/details/${medicalRecordId}/update`, detailData),
};

// Admin APIs
export const adminAPI = {
  getAllUsers: (page = 0, size = 10) => 
    api.get('/admin/user-account', { params: { page, size } }),
  promoteToEmployee: (userId) => 
    api.put(`/admin/create-employee-account/${userId}`),
};

// Employee Appointment APIs
export const employeeAppointmentAPI = {
  getAll: (page = 0, size = 10) => 
    api.get('/employees/appointments', { params: { page, size } }),
  create: (appointmentData) => 
    api.post('/employees/appointments/create', appointmentData),
  update: (appointmentId, appointmentData) => 
    api.patch(`/employees/appointments/update/${appointmentId}`, appointmentData),
};

// Employee Delivery APIs
export const employeeDeliveryAPI = {
  getDeliveryOrders: (page = 0, size = 10) =>
    api.get('/employees/delivery/orders', { params: { page, size } }),
  createDelivery: (deliveryData) =>
    api.post('/employees/delivery/orders', deliveryData),
  updateToDelivering: (orderNumber) =>
    api.put(`/employees/delivery/orders/delevering/${orderNumber}`),
  updateToDelivered: (orderNumber) =>
    api.put(`/employees/delivered/orders/delevering/${orderNumber}`),
};

// Customer Appointment APIs
export const customerAppointmentAPI = {
  getAppointments: (userId, page = 0, size = 10) =>
    api.get('/customers/appointments', { params: { userId, page, size } }),
};

// Employee Discount APIs
export const employeeDiscountAPI = {
  createManually: (discountData) => 
    api.post('/employees/discounts/create-manually', discountData),
  createAutomatically: (discountData) => 
    api.post('/employees/discounts/create-automatically', discountData),
  getByStatus: (status, page = 0, size = 10) => 
    api.get('/employees/discounts/status', { params: { status, page, size } }),
};

export default api;

