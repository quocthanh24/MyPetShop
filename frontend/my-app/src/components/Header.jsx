import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Check if current path is customer page
  const isCustomerPage = location.pathname === '/customer';
  const activeTab = new URLSearchParams(location.search).get('tab') || 'products';

  return (
    <header className="bg-white shadow-md">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <Link to="/" className="text-2xl font-bold text-primary-600">
            🐾 Pet Clinic
          </Link>
          
          <nav className="flex items-center space-x-6">
            {isAuthenticated && (
              <Link to="/" className="text-gray-700 hover:text-primary-600 transition">
                Trang Chủ
              </Link>
            )}
            {isAuthenticated && user?.role === 'CUSTOMER' && (
              <>
                <Link 
                  to="/customer?tab=products" 
                  className={`transition ${
                    isCustomerPage && activeTab === 'products'
                      ? 'text-primary-600 font-semibold border-b-2 border-primary-600'
                      : 'text-gray-700 hover:text-primary-600'
                  }`}
                >
                  Sản Phẩm
                </Link>
                <Link 
                  to="/customer?tab=cart" 
                  className={`transition ${
                    isCustomerPage && activeTab === 'cart'
                      ? 'text-primary-600 font-semibold border-b-2 border-primary-600'
                      : 'text-gray-700 hover:text-primary-600'
                  }`}
                >
                  Giỏ Hàng
                </Link>
                <Link 
                  to="/customer?tab=appointments" 
                  className={`transition ${
                    isCustomerPage && activeTab === 'appointments'
                      ? 'text-primary-600 font-semibold border-b-2 border-primary-600'
                      : 'text-gray-700 hover:text-primary-600'
                  }`}
                >
                  Lịch Hẹn
                </Link>
              </>
            )}
            {isAuthenticated && user?.role !== 'EMPLOYEE' && user?.role !== 'CUSTOMER' && user?.role !== 'ADMIN' && (
              <Link to="/appointments" className="text-gray-700 hover:text-primary-600 transition">
                Quản Lý Lịch Hẹn
              </Link>
            )}
            
            {isAuthenticated ? (
              <>
                {user?.role === 'ADMIN' && (
                  <Link to="/admin" className="text-gray-700 hover:text-primary-600 transition">
                    Admin
                  </Link>
                )}
                {user?.role === 'EMPLOYEE' && (
                  <Link 
                    to="/employee" 
                    className={`transition ${
                      location.pathname === '/employee'
                        ? 'text-primary-600 font-semibold border-b-2 border-primary-600'
                        : 'text-gray-700 hover:text-primary-600'
                    }`}
                  >
                    Quản Lý
                  </Link>
                )}
                <span className="text-gray-700">User ID: {user?.userId?.substring(0, 8)}...</span>
                <button
                  onClick={handleLogout}
                  className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition"
                >
                  Đăng Xuất
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-gray-700 hover:text-primary-600 transition">
                  Đăng Nhập
                </Link>
                <Link
                  to="/register"
                  className="bg-primary-600 text-white px-4 py-2 rounded hover:bg-primary-700 transition"
                >
                  Đăng Ký 
                </Link>
              </>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;

