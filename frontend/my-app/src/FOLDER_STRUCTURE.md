# React Folder Structure Guide

## 📁 Cấu Trúc Thư Mục Đã Refactor

```
src/
├── assets/                    # Static files (images, icons, etc)
├── components/                # Reusable components
│   ├── common/               # Common UI components
│   │   ├── Button.jsx
│   │   ├── Card.jsx
│   │   ├── Input.jsx
│   │   ├── Modal.jsx
│   │   ├── ProductImage.jsx
│   │   └── index.js          # Barrel export
│   ├── layout/               # Layout components
│   │   ├── Header.jsx
│   │   ├── Footer.jsx
│   │   └── index.js          # Barrel export
│   ├── features/             # Feature-specific components
│   │   └── ProtectedRoute.jsx
│   └── index.js              # Main barrel export
├── context/                  # React Context
│   └── AuthContext.jsx
├── hooks/                    # Custom hooks
│   ├── useAuth.js            # Auth hook
│   ├── useApi.js             # API hook
│   └── index.js              # Barrel export
├── layouts/                  # Page layouts
│   ├── MainLayout.jsx
│   └── AuthLayout.jsx
├── pages/                    # Page components
│   ├── auth/                 # Auth pages
│   │   ├── LoginPage.jsx
│   │   ├── RegisterPage.jsx
│   │   └── index.js
│   ├── customer/             # Customer pages
│   │   ├── CustomerPage.jsx
│   │   ├── ProductPage.jsx
│   │   └── index.js
│   ├── employee/             # Employee pages
│   │   ├── EmployeePage.jsx
│   │   ├── AppointmentPage.jsx
│   │   └── index.js
│   ├── admin/                # Admin pages
│   │   ├── AdminPage.jsx
│   │   ├── CategoryPage.jsx
│   │   └── index.js
│   ├── HomePage.jsx
│   ├── NotFoundPage.jsx
│   └── index.js
├── services/                 # API services
│   ├── api/                  # API modules
│   │   ├── customerProductAPI.js
│   │   ├── cartAPI.js
│   │   ├── orderAPI.js
│   │   ├── paymentAPI.js
│   │   ├── employeeProductAPI.js
│   │   ├── employeeDeliveryAPI.js
│   │   ├── medicalRecordAPI.js
│   │   ├── categoryAPI.js
│   │   ├── petAPI.js
│   │   └── index.js          # Barrel export
│   ├── axiosConfig.js        # Axios instance
│   └── index.js              # Main service barrel
├── styles/                   # Global styles
│   ├── global.css
│   └── variables.css
├── utils/                    # Utility functions
│   ├── validators.js
│   ├── formatters.js
│   ├── constants.js
│   └── index.js
├── App.jsx                   # Main App component
├── App.css
├── index.css
└── main.jsx
```

## ✨ Quy Ước & Best Practices

### 1. Imports
```javascript
// ❌ Tránh
import Button from '../../../components/Button';
import api from '../../../services/api';

// ✅ Tốt
import { Button, Card, Modal } from '@/components';
import { customerProductAPI, cartAPI } from '@/services/api';
```

### 2. Components
- **common/**: Button, Input, Card, Modal (dùng ở nhiều chỗ)
- **layout/**: Header, Footer, Sidebar (layout components)
- **features/**: ProtectedRoute, FeatureComponent (feature-specific)

### 3. Pages Organization
```
pages/
├── auth/       # LoginPage, RegisterPage
├── customer/   # Customer-specific pages
├── employee/   # Employee-specific pages
├── admin/      # Admin-specific pages
```

### 4. Services
Mỗi API endpoint group được tách ra thành file riêng:
```javascript
// services/api/customerProductAPI.js
export const customerProductAPI = { ... }

// services/api/cartAPI.js
export const cartAPI = { ... }
```

### 5. Custom Hooks
```javascript
// hooks/useAuth.js
export const useAuth = () => { ... }

// hooks/useApi.js
export const useApi = (endpoint) => { ... }
```

## 🔄 Cách Import Sau Refactor

### Barrel Exports (Giúp import dễ hơn)
```javascript
// Thay vì: import Button from './components/common/Button'
// Dùng:
import { Button, Card, Modal, Input } from '@/components';

// Thay vì: import customerProductAPI from './services/api/customerProductAPI'
// Dùng:
import { customerProductAPI, cartAPI } from '@/services/api';
```

## 📝 Lợi Ích của Cấu Trúc Này

✅ **Dễ bảo trì**: Code được tổ chức rõ ràng  
✅ **Dễ scale**: Thêm features mới không khó  
✅ **Dễ tìm**: Biết file nằm ở đâu  
✅ **Dễ test**: Components tách riêng  
✅ **Dễ reuse**: Barrel exports  

## 🚀 Next Steps

1. Cập nhật jsconfig.json hoặc vite.config.js để support alias:
```javascript
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  }
}
```

2. Update imports trong App.jsx và các pages
3. Move components vào đúng folder
4. Tạo custom hooks nếu cần
