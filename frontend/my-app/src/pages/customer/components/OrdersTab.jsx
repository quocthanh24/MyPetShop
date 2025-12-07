import { Card, Button } from '@/components';

/**
 * OrdersTab - Theo dõi đơn hàng
 * @param {Array} orders - Danh sách đơn hàng
 * @param {boolean} loading - Loading state
 * @param {Function} onCancelOrder - Cancel order callback
 * @param {number} totalPages - Tổng số trang
 * @param {number} currentPage - Trang hiện tại
 * @param {Function} onPageChange - Page change callback
 */
const OrdersTab = ({
  orders = [],
  loading = false,
  onCancelOrder,
  totalPages = 1,
  currentPage = 0,
  onPageChange,
}) => {
  if (loading) {
    return (
      <Card>
        <div className="text-center py-12">
          <p className="text-gray-500">Đang tải đơn hàng...</p>
        </div>
      </Card>
    );
  }

  if (orders.length === 0) {
    return (
      <Card>
        <div className="text-center py-12">
          <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p className="text-gray-500 text-lg">Không có đơn hàng nào</p>
          <p className="text-gray-400 text-sm">Bạn chưa đặt hàng, hãy mua sản phẩm ngay</p>
        </div>
      </Card>
    );
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'text-yellow-600 bg-yellow-100';
      case 'PAID':
        return 'text-blue-600 bg-blue-100';
      case 'DELIVERING':
        return 'text-indigo-600 bg-indigo-100';
      case 'DELIVERED':
        return 'text-green-600 bg-green-100';
      case 'CANCELLED':
        return 'text-red-600 bg-red-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'PENDING':
        return 'Chờ Xác Nhận';
      case 'PAID':
        return 'Đã Thanh Toán';
      case 'DELIVERING':
        return 'Đang Giao';
      case 'DELIVERED':
        return 'Đã Giao';
      case 'CANCELLED':
        return 'Đã Hủy';
      default:
        return status;
    }
  };

  return (
    <div className="space-y-4">
      {orders.map((order) => (
        <Card key={order.orderNumber} className="border-l-4 border-blue-500">
          <div className="flex justify-between items-start mb-4">
            <div>
              <h3 className="text-xl font-bold mb-2">Đơn #{order.orderNumber}</h3>
              <p className="text-sm text-gray-600">
                Ngày: {new Date(order.orderDate).toLocaleDateString('vi-VN')}
              </p>
            </div>
            <span className={`px-4 py-2 rounded-full text-sm font-semibold ${getStatusColor(order.orderStatus)}`}>
              {getStatusLabel(order.orderStatus)}
            </span>
          </div>

          <div className="mb-4 pb-4 border-b">
            <div className="flex justify-between mb-2">
              <span>Tổng tiền:</span>
              <span className="font-bold text-lg">
                {order.totalPrice?.toLocaleString('vi-VN')} VNĐ
              </span>
            </div>
          </div>

          <div className="flex gap-2">
            {order.orderStatus === 'PENDING' && (
              <Button
                variant="danger"
                onClick={() => onCancelOrder && onCancelOrder(order.orderNumber)}
              >
                Hủy Đơn
              </Button>
            )}
            <Button variant="secondary">Chi Tiết</Button>
          </div>
        </Card>
      ))}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-6">
          <Button
            variant="secondary"
            onClick={() => onPageChange && onPageChange(Math.max(0, currentPage - 1))}
            disabled={currentPage === 0}
          >
            Trước
          </Button>
          <span className="px-4 py-2">
            Trang {currentPage + 1} / {totalPages}
          </span>
          <Button
            variant="secondary"
            onClick={() => onPageChange && onPageChange(Math.min(totalPages - 1, currentPage + 1))}
            disabled={currentPage >= totalPages - 1}
          >
            Sau
          </Button>
        </div>
      )}
    </div>
  );
};

export default OrdersTab;
