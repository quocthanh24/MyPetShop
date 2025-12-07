# Pet Clinic Frontend

Ứng dụng ReactJS frontend cho phòng khám thú y.

## Cài đặt

1. Cài đặt dependencies:
```bash
npm install
```

2. Chạy ứng dụng ở chế độ development:
```bash
npm run dev
```

Ứng dụng sẽ chạy tại `http://localhost:3000`

## Build cho production

```bash
npm run build
```

## Cấu trúc dự án

```
frontend/
├── src/
│   ├── components/      # Các component tái sử dụng
│   │   ├── Header.jsx
│   │   ├── Footer.jsx
│   │   ├── Button.jsx
│   │   ├── Input.jsx
│   │   ├── Card.jsx
│   │   └── Modal.jsx
│   ├── pages/           # Các trang chính
│   │   ├── HomePage.jsx
│   │   ├── ProductPage.jsx
│   │   ├── AdminPage.jsx
│   │   ├── AppointmentPage.jsx
│   │   ├── LoginPage.jsx
│   │   ├── RegisterPage.jsx
│   │   └── NotFoundPage.jsx
│   ├── services/        # API services
│   │   └── api.js
│   ├── context/         # React Context
│   │   └── AuthContext.jsx
│   ├── App.jsx          # Main app component với routing
│   ├── main.jsx         # Entry point
│   └── index.css        # Global styles với TailwindCSS
├── package.json
├── vite.config.js
└── tailwind.config.js
```

## Tính năng

- ✅ Đăng nhập / Đăng ký
- ✅ Xem danh sách sản phẩm với tìm kiếm và lọc theo danh mục
- ✅ Thêm sản phẩm vào giỏ hàng
- ✅ Quản lý thú cưng và lịch hẹn
- ✅ Trang admin để quản lý người dùng và cấp quyền employee
- ✅ Responsive design với TailwindCSS

## API Endpoints

Backend API chạy tại `http://localhost:8080/api`

- `/api/login` - Đăng nhập
- `/api/register` - Đăng ký
- `/api/employees/products` - Quản lý sản phẩm
- `/api/employees/pets` - Quản lý thú cưng
- `/api/customers/add-to-cart` - Thêm vào giỏ hàng
- `/api/customers/place-order` - Đặt hàng
- `/api/admin/user-account` - Quản lý người dùng (Admin)

## Lưu ý

- Đảm bảo backend Spring Boot đang chạy tại `http://localhost:8080`
- Token được lưu trong localStorage
- Cần đăng nhập để sử dụng một số tính năng

