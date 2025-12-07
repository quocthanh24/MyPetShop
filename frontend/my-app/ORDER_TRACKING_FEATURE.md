# Order Tracking & Cancellation Feature

## Overview
Added complete order tracking and cancellation functionality to the "Đơn hàng" (Orders) tab in CustomerPage.

## Features Implemented

### 1. Load Orders (Tracking)
**Endpoint**: `GET /api/customers/orders/tracking?userId=xxx`

**Response Format** (Updated):
```json
{
    "content": [
        {
            "orderNumber": "ORD-20251120121015-8178",
            "orderDate": "2025-11-20T12:10:15.437131",
            "totalPrice": 398000.0
        },
        {
            "orderNumber": "ORD-20251120121346-3392",
            "orderDate": "2025-11-20T12:13:46.852235",
            "totalPrice": 398000.0
        }
    ],
    "page": {
        "size": 10,
        "number": 0,
        "totalElements": 2,
        "totalPages": 1
    }
}
```

**Fields**:
- `orderNumber`: Order ID (e.g., "ORD-20251120121015-8178")
- `orderDate`: ISO 8601 timestamp (e.g., "2025-11-20T12:10:15.437131")
- `totalPrice`: Total amount in VNĐ (e.g., 398000.0)

**Implementation**:
```jsx
const loadOrders = async () => {
  const userId = localStorage.getItem('userId');
  const response = await orderAPI.trackingOrders(userId, orderPage, 10);
  setOrders(response.data.content);
  setOrderTotalPages(response.data.page.totalPages);
}
```

**Auto-loads when**:
- Clicking on "Đơn hàng" tab
- Pagination changes
- After cancelling an order

---

### 2. Cancel Order
**Endpoint**: `POST /api/customers/orders/cancel?orderNumber=ORD-xxx`

**Rules**:
- Backend validates if order can be cancelled
- Returns 400 error if order cannot be cancelled
- Shows appropriate error messages

**Implementation**:
```jsx
const handleCancelOrder = async (orderNumber) => {
  if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) return;
  
  try {
    await orderAPI.cancelOrder(orderNumber);
    alert('✅ Hủy đơn hàng thành công!');
    loadOrders(); // Reload orders list
  } catch (error) {
    if (error.response?.status === 400) {
      alert('❌ Lỗi: Không thể hủy đơn hàng này (chỉ có thể hủy đơn hàng PENDING)');
    }
  }
}
```

---

## Order Display

### Helper Functions

**Format Order Date**:
```jsx
const formatOrderDate = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    return new Date(dateString).toLocaleString('vi-VN');
  } catch (error) {
    return dateString;
  }
};
```

Example conversion:
- API: `"2025-11-20T12:10:15.437131"`
- Display: `"20/11/2025, 12:10:15"`

---

## UI Components

### Orders List Display
```
┌─────────────────────────────────────────────┐
│ Đơn hàng #ORD-20251120121015-8178           │
│ Ngày đặt: 20/11/2025 12:10:15               │
│                                              │
│ Tổng tiền: 398.000 VNĐ                      │
│                                              │
│ ┌──────────────────┐                        │
│ │ Hủy Đơn Hàng     │  (always visible)     │
│ └──────────────────┘                        │
└─────────────────────────────────────────────┘
```

### Pagination
- Appears when `totalPages > 1`
- Shows current page / total pages
- Previous/Next buttons

### Empty State
- Icon + message when no orders exist
- Encourages user to add products to cart

---

## API Integration

### orderAPI methods used:
```javascript
// In api.js
export const orderAPI = {
  trackingOrders: (userId, page = 0, size = 10) => 
    api.get('/customers/orders/tracking', 
      { params: { userId, page, size } }),
  
  cancelOrder: (orderNumber) => 
    api.post('/customers/orders/cancel', null, 
      { params: { orderNumber } }),
}
```

---

## State Management

```jsx
// Order states
const [orders, setOrders] = useState([]); // List of orders
const [ordersLoading, setOrdersLoading] = useState(false);
const [orderPage, setOrderPage] = useState(0);
const [orderTotalPages, setOrderTotalPages] = useState(1);
```

**State Changes**:
1. Load orders → set from response.data.content
2. Cancel order → reload entire list
3. Pagination → change orderPage → trigger useEffect to reload

---

## Error Handling

### Network/Validation Errors:
```javascript
// 404 - Not found
if (error.response?.status === 404) {
  setOrders([]);
  // Show empty state
}

// 400 - Bad request (can't cancel)
if (error.response?.status === 400) {
  alert('❌ Lỗi: Không thể hủy đơn hàng này (chỉ có thể hủy đơn hàng PENDING)');
}

// Server message
if (error.response?.data?.message) {
  alert(`❌ Lỗi: ${error.response.data.message}`);
}

// Generic error
alert('❌ Có lỗi xảy ra khi [action]');
```

---

## Console Logging

Orders tracking:
```javascript
console.log('Orders tracking response:', response.data);
console.log('Cancelling order:', orderNumber);
console.error('Error cancelling order:', error);
```

---

## Testing Checklist

- [ ] Orders tab loads and displays all orders
- [ ] Order date displays correctly in Vietnamese format
- [ ] Order total price displays with comma separator (Vietnamese format)
- [ ] Pagination works correctly
- [ ] Cancel button always visible
- [ ] Click cancel → confirmation dialog appears
- [ ] Confirm cancel → success message → list refreshes
- [ ] Try cancel invalid order → error message appears
- [ ] Empty state shows when no orders
- [ ] User ID is retrieved from localStorage correctly
- [ ] Error handling displays appropriate messages

---

## Future Enhancements

- [ ] Order details modal (view order items, tracking number)
- [ ] Filter orders by date range
- [ ] Search orders by order number
- [ ] Download invoice
- [ ] Return/exchange request
- [ ] Real-time order status updates via WebSocket

