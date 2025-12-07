/**
 * Format tiền VND
 * @param {number} amount - Số tiền
 * @returns {string} Formatted price
 */
export const formatPrice = (amount) => {
  if (!amount) return '0 VNĐ';
  return `${amount.toLocaleString('vi-VN')} VNĐ`;
};

/**
 * Format ngày tháng
 * @param {string|Date} date - Ngày tháng
 * @returns {string} Formatted date
 */
export const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('vi-VN');
};

/**
 * Format số điện thoại
 * @param {string} phone - Số điện thoại
 * @returns {string} Formatted phone
 */
export const formatPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
};

/**
 * Cắt string dài
 * @param {string} str - String cần cắt
 * @param {number} length - Độ dài tối đa
 * @returns {string} Truncated string
 */
export const truncateString = (str, length = 50) => {
  if (!str || str.length <= length) return str;
  return str.substring(0, length) + '...';
};
