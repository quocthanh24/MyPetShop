# 📁 Project Structure Refactoring

## 🎯 Các Thay Đổi Chính

### 1. **Tổ Chức Components** 
- `components/common/` - Button, Card, Input, Modal, ProductImage
- `components/layout/` - Header, Footer  
- `components/features/` - ProtectedRoute, feature-specific components

### 2. **Tổ Chức Pages**
```
pages/
├── auth/          - LoginPage, RegisterPage
├── customer/      - CustomerPage, ProductPage
├── employee/      - EmployeePage, AppointmentPage
├── admin/         - AdminPage, CategoryPage
└── HomePage, NotFoundPage
```

### 3. **API Services**
```
services/api/
├── customerProductAPI.js
├── cartAPI.js
├── orderAPI.js
├── paymentAPI.js
├── employeeProductAPI.js
├── employeeDeliveryAPI.js
├── medicalRecordAPI.js
├── categoryAPI.js
├── petAPI.js
└── index.js (barrel export)
```

### 4. **Custom Hooks** (`hooks/`)
- `useAuth.js` - Authentication hook
- `useApi.js` - API data fetching hook
- Thêm custom hooks khác theo cần

### 5. **Utilities** (`utils/`)
- `validators.js` - Form validators
- `formatters.js` - Data formatters  
- `constants.js` - App constants
- `index.js` - Barrel export

### 6. **Styles** (`styles/`)
- `global.css` - Global styles
- `variables.css` - CSS variables

### 7. **Layouts** (`layouts/`)
- `MainLayout.jsx` - Layout cho main app
- `AuthLayout.jsx` - Layout cho auth pages

## 🔧 Setup Path Aliases

✅ **Đã thêm alias `@/` trong:**
- `vite.config.js` - Vite resolver
- `jsconfig.json` - IDE support

### Cách sử dụng:
```javascript
// ✅ Import dengan @/ alias
import { Button, Card } from '@/components';
import { useAuth } from '@/context/AuthContext';
import { customerProductAPI } from '@/services/api';
import { validateEmail } from '@/utils';

// ❌ Tránh relative paths dài
import Button from '../../../../components/Button';
```

## 📦 Barrel Exports

Các file `index.js` giúp import dễ hơn:

```javascript
// components/common/index.js
export { default as Button } from './Button';
export { default as Card } from './Card';
// ...

// ✅ Dùng:
import { Button, Card, Modal } from '@/components/common';
// Hoặc:
import { Button, Card } from '@/components';
```

## 🚀 Cách Migrate

### Step 1: Chạy dev server
```bash
npm run dev
```
Babel sẽ tự động recognize path aliases từ jsconfig.json

### Step 2: Update imports dần dần
```javascript
// OLD
import Button from './components/Button';

// NEW  
import { Button } from '@/components';
```

### Step 3: Organize code
- Move components vào đúng subfolder
- Group pages theo feature/role
- Tách services thành modules

## 📋 File Checklist

- [x] Tạo folder structure
- [x] Tạo barrel exports (index.js)
- [x] Cập nhật vite.config.js
- [x] Tạo jsconfig.json
- [x] Documentation

## ⚠️ Lưu Ý

1. **Node paths**: Đảm bảo đang dùng Vite (đã support alias)
2. **IDE Support**: Restart IDE sau khi update jsconfig.json
3. **Relative imports**: Có thể vẫn dùng được, nhưng tránh vì dễ bị lỗi refactor

## 💡 Best Practices

✅ Sắp xếp files theo feature/role  
✅ Dùng barrel exports (@/components, @/services)  
✅ 1 component = 1 file  
✅ Dùng index.js cho exports  
✅ Constants trong utils/constants.js  
✅ Custom hooks trong hooks/  

## 🎓 Tham Khảo

- [React Best Practices](https://react.dev/learn)
- [Project Structure Patterns](https://alexkondov.com/tao-good-developer/)
- [Vite Aliases](https://vitejs.dev/config/#resolve-alias)
