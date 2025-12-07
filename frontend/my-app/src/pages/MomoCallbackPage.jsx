import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import '../App.css';

export default function MomoCallbackPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('loading'); // loading, success, failed
  const [message, setMessage] = useState('Đang xử lý thanh toán...');
  const [orderData, setOrderData] = useState(null);

  useEffect(() => {
    const handleMomoCallback = async () => {
      try {
        // Lấy tất cả params từ URL
        const resultCode = searchParams.get('resultCode');
        const orderId = searchParams.get('orderId');
        const amount = searchParams.get('amount');
        const requestId = searchParams.get('requestId');
        const transId = searchParams.get('transId');
        const message_param = searchParams.get('message');
        const partnerCode = searchParams.get('partnerCode');
        const payType = searchParams.get('payType');
        const responseTime = searchParams.get('responseTime');

        console.log('═══════════════════════════════════════════════');
        console.log('🔔 MOMO Callback Received');
        console.log('═══════════════════════════════════════════════');
        console.log('📋 Full URL:', window.location.href);
        console.log('📊 Callback Params:', {
          resultCode,
          orderId,
          amount,
          requestId,
          transId,
          message: message_param,
          partnerCode,
          payType,
          responseTime,
        });
        console.log('═══════════════════════════════════════════════');

        setOrderData({
          orderId,
          amount,
          transId,
          requestId,
          payType,
          responseTime,
        });

        // resultCode = 0 là thành công
        if (resultCode === '0') {
          console.log('✅ Payment Successful! resultCode = 0');
          setStatus('success');
          setMessage('Thanh toán thành công! Đơn hàng đã được xác nhận.');
          
          // Redirect về trang khách hàng sau 2 giây
          setTimeout(() => {
            console.log('🔄 Redirecting to /customer');
            navigate('/customer');
          }, 2000);
        } else {
          console.log('❌ Payment Failed! resultCode =', resultCode);
          setStatus('failed');
          const errorMessages = {
            '1': 'Giao dịch đã bị từ chối',
            '9': 'Giao dịch bị hủy',
            '10': 'Xác thực không thành công',
            '11': 'Hết thời gian',
            '12': 'Thẻ/Tài khoản bị khóa',
            '13': 'Tài khoản không đủ tiền',
            '20': 'Tài khoản người dùng không hợp lệ',
            '21': 'Thông tin tài khoản không hợp lệ',
            '99': 'Lỗi khác',
          };
          
          const errorMessage = errorMessages[resultCode] || `Thanh toán thất bại (Mã lỗi: ${resultCode})`;
          console.log('Error message:', errorMessage);
          setMessage(errorMessage);

          // Redirect về customer sau 3 giây
          setTimeout(() => {
            console.log('🔄 Redirecting to /customer?tab=cart');
            navigate('/customer?tab=cart');
          }, 3000);
        }
      } catch (error) {
        console.error('❌ Error handling MOMO callback:', error);
        setStatus('failed');
        setMessage('Có lỗi xảy ra khi xử lý thanh toán');
        
        setTimeout(() => {
          navigate('/customer');
        }, 3000);
      }
    };

    handleMomoCallback();
  }, [searchParams, navigate]);

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-lg p-8 max-w-2xl w-full">
        {status === 'loading' && (
          <div className="text-center">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
            <h2 className="text-xl font-bold text-gray-800 mb-2">Đang xử lý...</h2>
            <p className="text-gray-600">{message}</p>
          </div>
        )}

        {status === 'success' && (
          <div className="text-center">
            <div className="text-5xl text-green-500 mb-4">✓</div>
            <h2 className="text-2xl font-bold text-green-600 mb-4">Thành công!</h2>
            <p className="text-gray-700 mb-6">{message}</p>
            
            {orderData && (
              <div className="bg-green-50 rounded p-4 mb-6 text-left text-sm border border-green-200">
                <p className="mb-2">
                  <strong>Mã đơn hàng:</strong> <code className="bg-gray-100 px-2 py-1 rounded">{orderData.orderId}</code>
                </p>
                <p className="mb-2">
                  <strong>Số tiền:</strong> <span className="text-green-600 font-bold">{Number(orderData.amount).toLocaleString('vi-VN')} VND</span>
                </p>
                <p className="mb-2">
                  <strong>Mã giao dịch:</strong> <code className="bg-gray-100 px-2 py-1 rounded text-xs">{orderData.transId}</code>
                </p>
                <p className="mb-2">
                  <strong>Request ID:</strong> <code className="bg-gray-100 px-2 py-1 rounded text-xs">{orderData.requestId}</code>
                </p>
                <p className="mb-2">
                  <strong>Phương thức:</strong> {orderData.payType}
                </p>
                <p className="text-xs text-gray-600 pt-2 border-t">
                  <strong>Thời gian:</strong> {new Date(Number(orderData.responseTime)).toLocaleString('vi-VN')}
                </p>
              </div>
            )}

            <p className="text-gray-600 text-sm mb-4">Chuyển hướng đến trang khách hàng...</p>

            {/* Debug Info */}
            <details className="mt-6 text-left bg-gray-50 p-3 rounded text-xs">
              <summary className="cursor-pointer font-mono text-gray-600 hover:text-gray-800">📋 Debug Info (Click để xem)</summary>
              <pre className="mt-2 bg-gray-900 text-green-400 p-3 rounded overflow-auto text-xs">
                {JSON.stringify(orderData, null, 2)}
              </pre>
            </details>
          </div>
        )}

        {status === 'failed' && (
          <div className="text-center">
            <div className="text-5xl text-red-500 mb-4">✕</div>
            <h2 className="text-2xl font-bold text-red-600 mb-4">Thất bại!</h2>
            <p className="text-gray-700 mb-6">{message}</p>
            
            {orderData && (
              <div className="bg-red-50 rounded p-4 mb-6 text-left text-sm border border-red-200">
                <p className="mb-2">
                  <strong>Mã đơn hàng:</strong> <code className="bg-gray-100 px-2 py-1 rounded">{orderData.orderId}</code>
                </p>
                <p className="mb-2">
                  <strong>Số tiền:</strong> {Number(orderData.amount).toLocaleString('vi-VN')} VND
                </p>
                <p className="mb-2">
                  <strong>Mã giao dịch:</strong> <code className="bg-gray-100 px-2 py-1 rounded text-xs">{orderData.transId}</code>
                </p>
              </div>
            )}

            <div className="flex gap-3 mb-4">
              <button
                onClick={() => navigate('/customer?tab=cart')}
                className="flex-1 bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition"
              >
                Quay lại giỏ hàng
              </button>
              <button
                onClick={() => navigate('/customer')}
                className="flex-1 bg-gray-600 text-white py-2 px-4 rounded hover:bg-gray-700 transition"
              >
                Về trang khách hàng
              </button>
            </div>

            <p className="text-gray-600 text-sm">Chuyển hướng tự động sau 3 giây...</p>

            {/* Debug Info */}
            <details className="mt-6 text-left bg-gray-50 p-3 rounded text-xs">
              <summary className="cursor-pointer font-mono text-gray-600 hover:text-gray-800">📋 Debug Info (Click để xem)</summary>
              <pre className="mt-2 bg-gray-900 text-red-400 p-3 rounded overflow-auto text-xs">
                {JSON.stringify(orderData, null, 2)}
              </pre>
            </details>
          </div>
        )}
      </div>
    </div>
  );
}
