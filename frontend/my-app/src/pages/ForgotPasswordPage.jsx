import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../services/api';
import Input from '../components/Input';
import Button from '../components/Button';
import Card from '../components/Card';

const ForgotPasswordPage = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1: Email, 2: OTP, 3: Reset Password
  const [formData, setFormData] = useState({
    gmail: '',
    otp: '',
    resetPassword: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
    setSuccess('');
  };

  // Step 1: Request OTP
  const handleRequestOtp = async (e) => {
    e.preventDefault();
    if (!formData.gmail.trim()) {
      setError('Vui lòng nhập email');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await authAPI.requestOtpResetPassword({
        gmail: formData.gmail.trim(),
      });

      if (response.data?.message) {
        setSuccess(response.data.message);
        setStep(2); // Move to OTP step
      } else {
        setError('Không thể gửi OTP. Vui lòng thử lại.');
      }
    } catch (error) {
      console.error('Error requesting OTP:', error);
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else {
        setError('Có lỗi xảy ra khi gửi OTP. Vui lòng thử lại.');
      }
    } finally {
      setLoading(false);
    }
  };

  // Step 2: Verify OTP
  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    if (!formData.otp.trim()) {
      setError('Vui lòng nhập mã OTP');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await authAPI.verifyOtp({
        gmail: formData.gmail.trim(),
        otp: formData.otp.trim(),
      });

      if (response.data?.message) {
        setSuccess(response.data.message);
        setStep(3); // Move to reset password step
      } else {
        setError('Mã OTP không hợp lệ.');
      }
    } catch (error) {
      console.error('Error verifying OTP:', error);
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else {
        setError('Mã OTP không hợp lệ. Vui lòng thử lại.');
      }
    } finally {
      setLoading(false);
    }
  };

  // Step 3: Reset Password
  const handleResetPassword = async (e) => {
    e.preventDefault();
    
    if (!formData.resetPassword.trim()) {
      setError('Vui lòng nhập mật khẩu mới');
      return;
    }

    if (formData.resetPassword.length < 6) {
      setError('Mật khẩu phải có ít nhất 6 ký tự');
      return;
    }

    if (formData.resetPassword !== formData.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await authAPI.resetPassword({
        gmail: formData.gmail.trim(),
        resetPassword: formData.resetPassword.trim(),
        confirmPassword: formData.confirmPassword.trim(),
      });

      if (response.data) {
        setSuccess('Đặt lại mật khẩu thành công! Đang chuyển đến trang đăng nhập...');
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setError('Không thể đặt lại mật khẩu. Vui lòng thử lại.');
      }
    } catch (error) {
      console.error('Error resetting password:', error);
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else {
        setError('Có lỗi xảy ra khi đặt lại mật khẩu. Vui lòng thử lại.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    if (step > 1) {
      setStep(step - 1);
      setError('');
      setSuccess('');
    } else {
      navigate('/login');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <Card className="w-full max-w-md">
        <h2 className="text-3xl font-bold text-center mb-2">Quên Mật Khẩu</h2>
        <p className="text-center text-gray-600 mb-6">
          {step === 1 && 'Nhập email để nhận mã OTP'}
          {step === 2 && 'Nhập mã OTP đã được gửi đến email của bạn'}
          {step === 3 && 'Đặt lại mật khẩu mới'}
        </p>

        {/* Progress Steps */}
        <div className="flex justify-center mb-6">
          <div className="flex items-center">
            <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${
              step >= 1 ? 'bg-primary-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              1
            </div>
            <div className={`w-16 h-1 ${step >= 2 ? 'bg-primary-600' : 'bg-gray-300'}`}></div>
            <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${
              step >= 2 ? 'bg-primary-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              2
            </div>
            <div className={`w-16 h-1 ${step >= 3 ? 'bg-primary-600' : 'bg-gray-300'}`}></div>
            <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${
              step >= 3 ? 'bg-primary-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              3
            </div>
          </div>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {success && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            {success}
          </div>
        )}

        {/* Step 1: Request OTP */}
        {step === 1 && (
          <form onSubmit={handleRequestOtp}>
            <Input
              label="Email"
              type="email"
              name="gmail"
              value={formData.gmail}
              onChange={handleChange}
              placeholder="your@email.com"
              required
            />

            <div className="flex gap-2 mt-4">
              <Button
                type="button"
                variant="secondary"
                onClick={handleBack}
                className="flex-1"
              >
                Quay lại
              </Button>
              <Button
                type="submit"
                className="flex-1"
                disabled={loading}
              >
                {loading ? 'Đang gửi...' : 'Gửi OTP'}
              </Button>
            </div>
          </form>
        )}

        {/* Step 2: Verify OTP */}
        {step === 2 && (
          <form onSubmit={handleVerifyOtp}>
            <Input
              label="Mã OTP"
              type="text"
              name="otp"
              value={formData.otp}
              onChange={handleChange}
              placeholder="Nhập mã OTP 6 số"
              required
              maxLength={6}
            />

            <p className="text-sm text-gray-600 mt-2 mb-4">
              Mã OTP đã được gửi đến: <strong>{formData.gmail}</strong>
            </p>

            <div className="flex gap-2 mt-4">
              <Button
                type="button"
                variant="secondary"
                onClick={handleBack}
                className="flex-1"
              >
                Quay lại
              </Button>
              <Button
                type="submit"
                className="flex-1"
                disabled={loading}
              >
                {loading ? 'Đang xác thực...' : 'Xác Thực OTP'}
              </Button>
            </div>

            <div className="mt-4 text-center">
              <button
                type="button"
                onClick={handleRequestOtp}
                className="text-sm text-primary-600 hover:underline"
              >
                Gửi lại mã OTP
              </button>
            </div>
          </form>
        )}

        {/* Step 3: Reset Password */}
        {step === 3 && (
          <form onSubmit={handleResetPassword}>
            <Input
              label="Mật khẩu mới"
              type="password"
              name="resetPassword"
              value={formData.resetPassword}
              onChange={handleChange}
              placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)"
              required
            />

            <Input
              label="Xác nhận mật khẩu"
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Nhập lại mật khẩu mới"
              required
            />

            <div className="flex gap-2 mt-4">
              <Button
                type="button"
                variant="secondary"
                onClick={handleBack}
                className="flex-1"
              >
                Quay lại
              </Button>
              <Button
                type="submit"
                className="flex-1"
                disabled={loading}
              >
                {loading ? 'Đang xử lý...' : 'Đặt Lại Mật Khẩu'}
              </Button>
            </div>
          </form>
        )}

        <div className="mt-6 text-center">
          <Link to="/login" className="text-primary-600 hover:underline text-sm">
            Quay lại trang đăng nhập
          </Link>
        </div>
      </Card>
    </div>
  );
};

export default ForgotPasswordPage;




