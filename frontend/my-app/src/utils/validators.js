/**
 * Validate email
 * @param {string} email - Email
 * @returns {boolean}
 */
export const validateEmail = (email) => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
};

/**
 * Validate phone number
 * @param {string} phone - Phone number
 * @returns {boolean}
 */
export const validatePhone = (phone) => {
  const regex = /^(\+84|0)(\d{9,10})$/;
  return regex.test(phone.replace(/[-\s]/g, ''));
};

/**
 * Validate password
 * @param {string} password - Password (min 6 chars)
 * @returns {boolean}
 */
export const validatePassword = (password) => {
  return password && password.length >= 6;
};

/**
 * Check if string is empty
 * @param {string} str - String
 * @returns {boolean}
 */
export const isEmpty = (str) => {
  return !str || str.trim().length === 0;
};
