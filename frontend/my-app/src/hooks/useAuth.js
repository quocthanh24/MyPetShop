import { useContext } from 'react';
import { AuthContext } from '@/context/AuthContext';

/**
 * Custom hook để dùng Auth Context
 * @returns {Object} { user, isAuthenticated, login, logout }
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
