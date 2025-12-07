import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, requireAdmin = false, requireEmployee = false, requireCustomer = false }) => {
  const { isAuthenticated, loading, user } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Đang tải...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin && user?.role !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }

  if (requireEmployee && user?.role !== 'EMPLOYEE') {
    return <Navigate to="/" replace />;
  }

  if (requireCustomer && user?.role !== 'CUSTOMER') {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;

