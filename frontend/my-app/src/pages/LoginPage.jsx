import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Input from '../components/Input';
import Button from '../components/Button';
import Card from '../components/Card';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    gmail: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    const result = await login(formData);
    
    if (result.success) {
      // Redirect based on role
      if (result.role === 'ADMIN') {
        navigate('/admin');
      } else if (result.role === 'EMPLOYEE') {
        navigate('/categories');
      } else {
        navigate('/');
      }
    } else {
      setError(result.error || 'Đăng nhập thất bại');
    }
    
    setLoading(false);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <Card className="w-full max-w-md">
        <h2 className="text-3xl font-bold text-center mb-6">Đăng Nhập</h2>
        
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <Input
            label="Email"
            type="email"
            name="gmail"
            value={formData.gmail}
            onChange={handleChange}
            placeholder="your@email.com"
            required
          />

          <Input
            label="Mật khẩu"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Nhập mật khẩu"
            required
          />

          <Button
            type="submit"
            className="w-full mb-4"
            disabled={loading}
          >
            {loading ? 'Đang đăng nhập...' : 'Đăng Nhập'}
          </Button>
        </form>

        <div className="text-center mb-4">
          <Link to="/forgot-password" className="text-primary-600 hover:underline text-sm">
            Quên mật khẩu?
          </Link>
        </div>

        <p className="text-center text-gray-600">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="text-primary-600 hover:underline">
            Đăng ký ngay
          </Link>
        </p>
      </Card>
    </div>
  );
};

export default LoginPage;

