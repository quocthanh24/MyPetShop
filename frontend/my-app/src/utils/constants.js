/**
 * App Constants
 */

// API
export const API_BASE_URL = 'http://localhost:8080/api';

// Auth
export const TOKEN_KEY = 'accessToken';
export const USER_ID_KEY = 'userId';
export const USER_ROLE_KEY = 'userRole';

// Roles
export const ROLES = {
  ADMIN: 'ADMIN',
  CUSTOMER: 'CUSTOMER',
  EMPLOYEE: 'EMPLOYEE',
};

// Pet Types
export const PET_TYPES = ['CAT', 'DOG', 'BIRD', 'OTHER'];

// Order Status
export const ORDER_STATUS = {
  PENDING: 'PENDING',
  PAID: 'PAID',
  DELIVERING: 'DELIVERING',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED',
};

// Required Note (Delivery)
export const REQUIRED_NOTE = {
  ALLOW_TRY: 'CHOTHUHANG',
  VIEW_NO_TRY: 'CHOXEMHANGKHONTHU',
  NO_VIEW: 'KHONGCHOXEMHANG',
};

// Pagination
export const DEFAULT_PAGE_SIZE = 10;
