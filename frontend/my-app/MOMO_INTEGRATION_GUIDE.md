# MOMO Payment Integration - Setup Guide

## Frontend Setup (Completed ✓)

### 1. MomoCallbackPage Component
- **File**: `src/pages/MomoCallbackPage.jsx`
- **Route**: `/momo-callback`
- **Tính năng**:
  - Nhận callback từ MOMO qua query parameters
  - Parse `resultCode` để xác định kết quả thanh toán
  - Hiển thị trang thành công/thất bại
  - Tự động chuyển hướng đến `/customer?tab=orders` (thành công) hoặc `/customer?tab=cart` (thất bại)
  - Hiển thị thông tin đơn hàng: mã đơn, số tiền, mã giao dịch

### 2. Response Codes từ MOMO
```
resultCode = 0      → Thành công (PAID)
resultCode = 1      → Giao dịch bị từ chối
resultCode = 9      → Giao dịch bị hủy
resultCode = 10     → Xác thực không thành công
resultCode = 11     → Hết thời gian
resultCode = 12     → Thẻ/Tài khoản bị khóa
resultCode = 13     → Tài khoản không đủ tiền
resultCode = 20     → Tài khoản người dùng không hợp lệ
resultCode = 21     → Thông tin tài khoản không hợp lệ
resultCode = 99     → Lỗi khác
```

## Backend Setup (Required ⚠️)

### 1. Application Properties Configuration
Thêm vào `application.properties` hoặc `application.yml`:

```properties
# MOMO Payment Configuration
momo.endpoint=https://test-payment.momo.vn/v3/gateway/api/create
momo.partnerCode=MOMOLRJZ20181206
momo.accessKey=mTCKt9W3eU1m39TW
momo.secretKey=05f3d0a58229234d3aba83d4da6d92e2
momo.partnerName=Test

# ⚠️ CRITICAL: Update these URLs to match your deployment
# For Development/Testing
momo.redirectUrl=http://localhost:3000/momo-callback
momo.ipnUrl=http://localhost:8080/api/payment/momo/ipn

# For Production (update domain)
# momo.redirectUrl=https://yourdomain.com/momo-callback
# momo.ipnUrl=https://yourdomain.com/api/payment/momo/ipn
```

### 2. Environment Variables
Hoặc sử dụng environment variables (recommended for security):
```bash
MOMO_ENDPOINT=https://test-payment.momo.vn/v3/gateway/api/create
MOMO_PARTNER_CODE=MOMOLRJZ20181206
MOMO_ACCESS_KEY=mTCKt9W3eU1m39TW
MOMO_SECRET_KEY=05f3d0a58229234d3aba83d4da6d92e2
MOMO_REDIRECT_URL=http://localhost:3000/momo-callback
MOMO_IPN_URL=http://localhost:8080/api/payment/momo/ipn
```

### 3. Controller Setup
Tạo endpoint để nhận IPN callback từ MOMO:

```java
@RestController
@RequestMapping("/api/payment/momo")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/ipn")
    public ResponseEntity<?> handleMomoIpn(@RequestBody Map<String, Object> payload) {
        try {
            String result = paymentService.momoIpn(payload);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestParam String orderId,
            @RequestParam String amount) {
        try {
            Map<String, Object> response = paymentService.createPayment(orderId, amount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
```

## Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                  MOMO Payment Flow                           │
└─────────────────────────────────────────────────────────────┘

1. Frontend - Place Order
   └─ Call: POST /api/customers/orders?userId=xxx
   └─ Response: { orderNumber, totalPrice, ... }

2. Frontend - MOMO Payment
   └─ Call: POST /api/payment/momo/create?orderId=xxx&amount=yyy
   └─ Response: { payUrl, requestId, ... }
   └─ User redirects to: payUrl (MOMO Gateway)

3. User Payment on MOMO
   └─ User completes payment on MOMO website

4. MOMO Callbacks
   ├─ IPN Callback (Server-to-Server)
   │  └─ POST /api/payment/momo/ipn
   │  └─ Backend updates order status (PAID / PAYMENT_FAILED)
   │
   └─ Redirect User (User Browser)
      └─ GET /momo-callback?resultCode=0&orderId=xxx&amount=yyy&transId=zzz
      └─ Frontend shows result page
      └─ Auto-redirect to /customer?tab=orders

5. Result
   ├─ Success: Order marked as PAID, awaiting fulfillment
   └─ Failed: Order marked as PAYMENT_FAILED, user can retry
```

## Testing Checklist

- [ ] Backend `redirectUrl` points to `http://localhost:5173/momo-callback`
- [ ] Backend `ipnUrl` points to `http://localhost:8080/api/payment/momo/ipn`
- [ ] `MomoCallbackPage` route is registered in `App.jsx`
- [ ] `updateOrderStatus` method exists in backend
- [ ] Test with MOMO test account
- [ ] Verify `resultCode=0` triggers order status update to PAID
- [ ] Verify `resultCode≠0` triggers order status update to PAYMENT_FAILED
- [ ] Test auto-redirect after payment completes

## Common Issues

### Issue: "Signature Mismatch"
**Solution**: Ensure `secretKey` matches MOMO partner credentials

### Issue: "IPN not called"
**Solution**: 
- Verify firewall allows MOMO server to reach `ipnUrl`
- Check `ipnUrl` is publicly accessible (not localhost for production)
- Verify URL in MOMO merchant dashboard

### Issue: "Order status not updated"
**Solution**: 
- Verify `orderService.updateOrderStatus()` exists
- Check logs for IPN response details
- Ensure resultCode is correctly parsed

### Issue: "Redirect URL not working"
**Solution**: 
- Ensure frontend `redirectUrl` in backend config matches deployed URL
- Test with `http://localhost:5173/momo-callback` for local development
- For production: update to your actual domain

