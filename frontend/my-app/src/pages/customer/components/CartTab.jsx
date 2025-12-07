import { Card, Button } from '@/components';

/**
 * CartTab - Quản lý giỏ hàng
 * @param {Array} cartItems - Items trong giỏ
 * @param {Object} cartData - Cart totals & info
 * @param {boolean} loading - Loading state
 * @param {Function} onUpdateItem - Update quantity callback
 * @param {Function} onRemoveItem - Remove item callback
 * @param {Function} onCheckout - Checkout callback
 */
const CartTab = ({
  cartItems = [],
  cartData = { totalPrice: 0, discountAmount: 0 },
  loading = false,
  onUpdateItem,
  onRemoveItem,
  onCheckout,
}) => {
  if (loading) {
    return (
      <Card>
        <div className="text-center py-12">
          <p className="text-gray-500">Đang tải giỏ hàng...</p>
        </div>
      </Card>
    );
  }

  if (cartItems.length === 0) {
    return (
      <Card>
        <div className="text-center py-12">
          <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <p className="text-gray-500 text-lg">Giỏ hàng trống</p>
          <p className="text-gray-400 text-sm">Thêm sản phẩm vào giỏ để tiếp tục mua hàng</p>
        </div>
      </Card>
    );
  }

  const finalTotal = (cartData.totalPrice || 0) - (cartData.discountAmount || 0);

  return (
    <div className="space-y-6">
      <Card>
        <h2 className="text-2xl font-bold mb-6">Giỏ Hàng Của Bạn</h2>

        <div className="space-y-4">
          {cartItems.map((item) => (
            <div
              key={item.cartItemId || item.productId}
              className="flex items-center gap-4 p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition"
            >
              <div className="w-20 h-20 bg-gray-200 rounded flex items-center justify-center">
                <span className="text-xs text-gray-400">IMG</span>
              </div>

              <div className="flex-1">
                <h3 className="font-bold text-lg">{item.productName || item.name}</h3>
                {item.price && (
                  <p className="text-blue-600 font-semibold">
                    {item.price.toLocaleString('vi-VN')} VNĐ
                  </p>
                )}
              </div>

              <div className="flex items-center gap-2">
                <Button
                  variant="secondary"
                  onClick={() => onUpdateItem && onUpdateItem(item.cartItemId, (item.quantity || 1) - 1)}
                  disabled={item.quantity <= 1}
                  size="sm"
                >
                  −
                </Button>
                <span className="px-4 py-2 font-semibold">{item.quantity || 1}</span>
                <Button
                  variant="secondary"
                  onClick={() => onUpdateItem && onUpdateItem(item.cartItemId, (item.quantity || 1) + 1)}
                  size="sm"
                >
                  +
                </Button>
              </div>

              <div className="text-right">
                <p className="font-bold text-lg">
                  {((item.price || 0) * (item.quantity || 1)).toLocaleString('vi-VN')} VNĐ
                </p>
                <Button
                  variant="danger"
                  onClick={() => onRemoveItem && onRemoveItem(item.cartItemId)}
                  size="sm"
                >
                  Xóa
                </Button>
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* Checkout Section */}
      <Card>
        <h3 className="text-xl font-bold mb-4">Tóm Tắt Đơn Hàng</h3>

        <div className="space-y-2 mb-6 border-b pb-4">
          <div className="flex justify-between">
            <span>Tổng tiền:</span>
            <span className="font-semibold">{(cartData.totalPrice || 0).toLocaleString('vi-VN')} VNĐ</span>
          </div>

          {cartData.discountAmount > 0 && (
            <div className="flex justify-between text-green-600">
              <span>Giảm giá:</span>
              <span className="font-semibold">-{cartData.discountAmount.toLocaleString('vi-VN')} VNĐ</span>
            </div>
          )}

          <div className="flex justify-between text-lg font-bold">
            <span>Thành tiền:</span>
            <span className="text-blue-600">{finalTotal.toLocaleString('vi-VN')} VNĐ</span>
          </div>
        </div>

        <div className="space-y-2">
          <Button onClick={() => onCheckout && onCheckout('CASH')} className="w-full">
            Thanh Toán Bằng Tiền Mặt
          </Button>
          <Button onClick={() => onCheckout && onCheckout('MOMO')} variant="secondary" className="w-full">
            Thanh Toán Bằng MOMO
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default CartTab;
