# MOMO Payment Testing Checklist

## Test Flow

### 1️⃣ Frontend - Place Order
```
✓ Trang Customer Page
✓ Chọn sản phẩm, thêm vào giỏ hàng
✓ Click "Thanh toán" hoặc "Đặt hàng ngay"
✓ Chọn payment method: MOMO
✓ Điền form checkout (địa chỉ, số điện thoại)
✓ Có thể thêm mã giảm giá (lowercase/uppercase đều được)
✓ Click "Đặt hàng"
```

**Expected Console Logs:**
```
✅ Order response: { orderNumber, totalPrice, ... }
✅ Creating MOMO payment with orderNumber + totalPrice (after discount)
✅ MOMO payment response: { payUrl, ... }
✅ Browser redirects to MOMO gateway
```

---

### 2️⃣ MOMO Payment
```
✓ User completes payment on MOMO website
✓ MOMO redirects user back to frontend
```

**URL will look like:**
```
http://localhost:3000/momo-callback?
  resultCode=0&
  orderId=ORD-20251120120249-7344&
  amount=199000&
  transId=4612691150&
  ...
```

---

### 3️⃣ Frontend - Callback Handler (MomoCallbackPage)

**Browser Console Should Show:**
```
═══════════════════════════════════════════════
🔔 MOMO Callback Received
═══════════════════════════════════════════════
📋 Full URL: http://localhost:3000/momo-callback?resultCode=0&...
📊 Callback Params: {
  resultCode: "0",
  orderId: "ORD-20251120120249-7344",
  amount: "199000",
  requestId: "...",
  transId: "4612691150",
  message: "Successful.",
  ...
}
═══════════════════════════════════════════════

✅ Payment Successful! resultCode = 0
🔄 Redirecting to /customer?tab=orders
```

**UI Should Show:**
- ✓ Big green checkmark (✓)
- ✓ "Thành công!" heading
- ✓ "Thanh toán thành công! Đơn hàng đã được xác nhận."
- ✓ Order details card (green background)
  - Mã đơn hàng: ORD-20251120120249-7344
  - Số tiền: 199.000 VND (or with discount applied)
  - Mã giao dịch: 4612691150
  - Thời gian: [timestamp]
- ✓ Debug Info section (collapsible)
- ✓ Auto-redirect message

---

### 4️⃣ Backend - IPN Callback

**Backend Logs Should Show:**
```
2024-11-20 12:02:55 INFO [PaymentService] 
  🔔 IPN Callback received! 
  orderId: ORD-20251120120249-7344, 
  resultCode: 0

2024-11-20 12:02:55 INFO [PaymentService]
  Signature verified successfully

2024-11-20 12:02:55 INFO [OrderService]
  ✅ Order status updated: ORD-20251120120249-7344 → PAID
```

**Database Should Show:**
```
orders table:
  - order_number: ORD-20251120120249-7344
  - order_status: PAID (or PAYMENT_PAID)
  - total_price: 199000 (or after discount)
```

---

### 5️⃣ Frontend - Orders Page

**After redirect to /customer?tab=orders:**
- ✓ Order appears in "Danh sách đơn hàng" (Orders List)
- ✓ Status shows: "Đã thanh toán" or "PAID"
- ✓ Amount shows: 199.000 VND (including discount if applied)
- ✓ Transaction ID shows: 4612691150

---

## Error Scenarios

### ❌ If Payment Fails (resultCode ≠ 0)

**URL Example:**
```
http://localhost:3000/momo-callback?resultCode=13&orderId=...
```

**Console Should Show:**
```
❌ Payment Failed! resultCode = 13

Error message: Tài khoản không đủ tiền
```

**UI Should Show:**
- ✓ Big red X (✕)
- ✓ "Thất bại!" heading
- ✓ Error message
- ✓ Two buttons:
  - "Quay lại giỏ hàng"
  - "Xem đơn hàng"
- ✓ Auto-redirect to /customer?tab=cart

**Backend Should Show:**
```
Order status updated: ORD-xxx → PAYMENT_FAILED
```

---

## Discount Code Testing

### Scenario 1: Uppercase Discount Code
```
Input: "SUMMER2024"
Amount before discount: 200.000 VND
Amount after discount: 180.000 VND
MOMO payment should use: 180.000 VND
```

**Verify:**
- Order response includes totalPrice: 180000
- MOMO payment receives: 180000 (not 200000)
- Database stores discountCode: "SUMMER2024"

### Scenario 2: Lowercase/Mixed Case Discount Code
```
Input: "summer2024" or "SuMmEr2024"
Result: Should work same as uppercase
```

**Console Should Show:**
```
Creating order with discountCode: "summer2024"
```

---

## Common Test Cases

| Test Case | Input | Expected Result |
|-----------|-------|-----------------|
| Valid payment | resultCode=0 | Green success page, order marked PAID |
| Insufficient funds | resultCode=13 | Red failure page, order marked PAYMENT_FAILED |
| User cancels | resultCode=9 | Red failure page, redirects to cart |
| Network timeout | resultCode=99 | Red failure page, can retry |
| Discount applied | "DISC50" | Price reduced in MOMO payment |
| No discount | Empty | Full price to MOMO |
| Buy-now with discount | resultCode=0 | Success, discount applied to buy-now order |

---

## Debug Checklist

- [ ] Browser DevTools Console shows callback params
- [ ] All query parameters received: resultCode, orderId, amount, transId, etc.
- [ ] Correct status (success/failed) based on resultCode
- [ ] Auto-redirect happens after 2-3 seconds
- [ ] Backend logs show IPN received
- [ ] Signature verification passes in backend
- [ ] Order status updated in database
- [ ] Order appears in Orders List with correct status
- [ ] Discount code applied correctly to payment amount
- [ ] Error messages display for failed payments

---

## Quick Debug Tips

### 1. Check Frontend Callback
```javascript
// Open browser console (F12)
// Go to /momo-callback URL
// Look for:
console.log('🔔 MOMO Callback Received')
console.log('✅ Payment Successful')
// or
console.log('❌ Payment Failed')
```

### 2. Check Backend IPN
```bash
# Check backend logs
tail -f logs/application.log

# Look for:
# "IPN Callback received"
# "Signature verified"
# "Order status updated"
```

### 3. Verify Database
```sql
SELECT * FROM orders 
WHERE order_number = 'ORD-20251120120249-7344';
-- Should show: order_status = 'PAID'
```

### 4. Test Payment Amount
```javascript
// In frontend console at checkout
console.log('Order total:', orderResponse.data.totalPrice);
// Should match MOMO payment amount (after discount)
```

