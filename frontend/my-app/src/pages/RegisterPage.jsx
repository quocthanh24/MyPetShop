import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../services/api';
import Input from '../components/Input';
import Button from '../components/Button';
import Card from '../components/Card';

const RegisterPage = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1: Enter info and request OTP, 2: Verify OTP
  const [formData, setFormData] = useState({
    gmail: '',
    password: '',
    fullName: '',
    gender: 'Nam',
    phoneNumber: '',
    address: '',
    dob: '',
  });
  const [otpCode, setOtpCode] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [otpSent, setOtpSent] = useState(false);
  const [countdown, setCountdown] = useState(0);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const handleRequestOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Validate required fields for step 1
    if (!formData.gmail.trim()) {
      setError('Vui lòng nhập email');
      setLoading(false);
      return;
    }
    if (!formData.fullName.trim()) {
      setError('Vui lòng nhập họ và tên');
      setLoading(false);
      return;
    }

    try {
      const response = await authAPI.requestOtp({
        gmail: formData.gmail.trim(),
        fullName: formData.fullName.trim(),
      });

      if (response.data) {
        setOtpSent(true);
        setStep(2);
        // Start countdown (60 seconds)
        setCountdown(60);
        const timer = setInterval(() => {
          setCountdown((prev) => {
            if (prev <= 1) {
              clearInterval(timer);
              return 0;
            }
            return prev - 1;
          });
        }, 1000);
        alert('OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.');
      }
    } catch (error) {
      console.error('Error requesting OTP:', error);
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else if (error.response?.data) {
        // Handle error object
        const errorMsg = typeof error.response.data === 'string' 
          ? error.response.data 
          : error.response.data.message || 'Có lỗi xảy ra khi gửi OTP';
        setError(errorMsg);
      } else {
        setError('Có lỗi xảy ra khi gửi OTP. Vui lòng thử lại.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    if (countdown > 0) {
      alert(`Vui lòng đợi ${countdown} giây trước khi gửi lại OTP`);
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await authAPI.requestOtp({
        gmail: formData.gmail.trim(),
        fullName: formData.fullName.trim(),
      });

      if (response.data) {
        setCountdown(60);
        const timer = setInterval(() => {
          setCountdown((prev) => {
            if (prev <= 1) {
              clearInterval(timer);
              return 0;
            }
            return prev - 1;
          });
        }, 1000);
        alert('OTP đã được gửi lại đến email của bạn.');
      }
    } catch (error) {
      console.error('Error resending OTP:', error);
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else {
        setError('Có lỗi xảy ra khi gửi lại OTP. Vui lòng thử lại.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Validate all fields
    if (!formData.gmail.trim()) {
      setError('Vui lòng nhập email');
      setLoading(false);
      return;
    }
    if (!formData.password.trim()) {
      setError('Vui lòng nhập mật khẩu');
      setLoading(false);
      return;
    }
    if (!formData.fullName.trim()) {
      setError('Vui lòng nhập họ và tên');
      setLoading(false);
      return;
    }
    if (!formData.phoneNumber.trim()) {
      setError('Vui lòng nhập số điện thoại');
      setLoading(false);
      return;
    }
    if (!formData.address.trim()) {
      setError('Vui lòng nhập địa chỉ');
      setLoading(false);
      return;
    }
    if (!formData.dob) {
      setError('Vui lòng chọn ngày sinh');
      setLoading(false);
      return;
    }
    if (!otpCode.trim()) {
      setError('Vui lòng nhập mã OTP');
      setLoading(false);
      return;
    }

    try {
      // Format dob as YYYY-MM-DD (backend expects Date)
      const registerData = {
        gmail: formData.gmail.trim(),
        password: formData.password,
        fullName: formData.fullName.trim(),
        gender: formData.gender,
        phoneNumber: formData.phoneNumber.trim(),
        address: formData.address.trim(),
        dob: formData.dob, // Backend will parse this as Date
        otpCode: otpCode.trim(),
      };

      const response = await authAPI.registerWithOtp(registerData);

      if (response.data) {
        alert('Đăng ký thành công! Vui lòng đăng nhập.');
        navigate('/login');
      }
    } catch (error) {
      console.error('Error registering:', error);
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else if (error.response?.data) {
        const errorMsg = typeof error.response.data === 'string' 
          ? error.response.data 
          : error.response.data.message || 'Đăng ký thất bại';
        setError(errorMsg);
      } else {
        setError('Đăng ký thất bại. Vui lòng thử lại.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <Card className="w-full max-w-md">
        <h2 className="text-3xl font-bold text-center mb-6">Đăng Ký</h2>
        
        {/* Step Indicator */}
        <div className="flex justify-center mb-6">
          <div className="flex items-center">
            <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
              step >= 1 ? 'bg-primary-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              1
            </div>
            <div className={`w-16 h-1 mx-2 ${
              step >= 2 ? 'bg-primary-600' : 'bg-gray-300'
            }`}></div>
            <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
              step >= 2 ? 'bg-primary-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              2
            </div>
          </div>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {step === 1 ? (
          // Step 1: Enter info and request OTP
          <form onSubmit={handleRequestOtp}>
            <Input
              label="Họ và tên"
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Nguyễn Thế Hưng"
              required
            />

            <Input
              label="Email"
              type="email"
              name="gmail"
              value={formData.gmail}
              onChange={handleChange}
              placeholder="your@email.com"
              required
            />

            <Button
              type="submit"
              className="w-full mb-4"
              disabled={loading}
            >
              {loading ? 'Đang gửi OTP...' : 'Gửi Mã OTP'}
            </Button>

            <p className="text-sm text-gray-600 text-center mb-4">
              Bạn sẽ nhận được mã OTP qua email để xác nhận đăng ký.
            </p>
          </form>
        ) : (
          // Step 2: Complete registration with OTP
          <form onSubmit={handleRegister}>
            <div className="mb-4 p-4 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-800">
                <strong>Email:</strong> {formData.gmail}
              </p>
              <p className="text-sm text-blue-800">
                <strong>Họ tên:</strong> {formData.fullName}
              </p>
            </div>

            <Input
              label="Mã OTP"
              type="text"
              name="otpCode"
              value={otpCode}
              onChange={(e) => {
                setOtpCode(e.target.value);
                setError('');
              }}
              placeholder="Nhập mã OTP"
              required
              maxLength={6}
            />

            <div className="mb-4">
              {countdown > 0 ? (
                <p className="text-sm text-gray-600 text-center">
                  Gửi lại OTP sau {countdown} giây
                </p>
              ) : (
                <Button
                  type="button"
                  variant="secondary"
                  className="w-full mb-4"
                  onClick={handleResendOtp}
                  disabled={loading}
                >
                  Gửi Lại OTP
                </Button>
              )}
            </div>

            <Input
              label="Mật khẩu"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Nhập mật khẩu"
              required
            />

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Giới tính <span className="text-red-500">*</span>
              </label>
              <select
                name="gender"
                value={formData.gender}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              >
                <option value="Nam">Nam</option>
                <option value="Nữ">Nữ</option>
              </select>
            </div>

            <Input
              label="Số điện thoại"
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="0356326721"
              required
            />

            <Input
              label="Địa chỉ"
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              placeholder="Số 456, Đường Trần Hưng Đạo, Phường Phú Trinh, TP. Phan Thiết, Tỉnh Bình Thuận"
              required
            />

            <Input
              label="Ngày sinh"
              type="date"
              name="dob"
              value={formData.dob}
              onChange={handleChange}
              required
            />

            <div className="flex gap-2">
              <Button
                type="button"
                variant="secondary"
                className="flex-1"
                onClick={() => {
                  setStep(1);
                  setOtpCode('');
                  setError('');
                }}
                disabled={loading}
              >
                Quay Lại
              </Button>
              <Button
                type="submit"
                className="flex-1"
                disabled={loading}
              >
                {loading ? 'Đang đăng ký...' : 'Đăng Ký'}
              </Button>
            </div>
          </form>
        )}

        <p className="text-center text-gray-600 mt-4">
          Đã có tài khoản?{' '}
          <Link to="/login" className="text-primary-600 hover:underline">
            Đăng nhập ngay
          </Link>
        </p>
      </Card>
    </div>
  );
};

export default RegisterPage;
