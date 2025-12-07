# ✅ Page Components Breakdown - Quick Reference

## 📦 Cấu Trúc Mới

### CustomerPage
```
pages/customer/
├── CustomerPage.jsx          (Main - 1783 → ~800 dòng sau tách)
├── components/
│   ├── ProductTab.jsx        ✅ (Danh sách & tìm kiếm sản phẩm)
│   ├── CartTab.jsx           ✅ (Giỏ hàng & checkout)
│   ├── OrdersTab.jsx         ✅ (Theo dõi đơn hàng)
│   ├── PetsTab.jsx           📋 (Chưa tạo)
│   ├── ProfileTab.jsx        📋 (Chưa tạo)
│   └── index.js
└── index.js
```

### EmployeePage  
```
pages/employee/
├── EmployeePage.jsx          (Main - 2373 → ~1200 dòng sau tách)
├── components/
│   ├── ProductsTab.jsx       📋 (Quản lý sản phẩm)
│   ├── MedicalRecordsTab.jsx 📋 (Quản lý bệnh án)
│   ├── DeliveryTab.jsx       📋 (Quản lý giao hàng)
│   └── index.js
└── index.js
```

## 🔄 Migration Flow

### Bước 1: CustomerPage (Đang làm)
```javascript
// Trước
<div>
  {activeTab === 'products' && <div>...800 dòng</div>}
  {activeTab === 'cart' && <div>...400 dòng</div>}
  {activeTab === 'orders' && <div>...300 dòng</div>}
</div>

// Sau
<div>
  {activeTab === 'products' && <ProductTab {...props} />}
  {activeTab === 'cart' && <CartTab {...props} />}
  {activeTab === 'orders' && <OrdersTab {...props} />}
</div>
```

### Bước 2: EmployeePage (Tương tự)
```javascript
// Tách thành:
{activeTab === 'products' && <ProductsTab {...props} />}
{activeTab === 'medicalRecords' && <MedicalRecordsTab {...props} />}
{activeTab === 'delivery' && <DeliveryTab {...props} />}
```

## ✨ Lợi Ích

| Trước | Sau |
|------|-----|
| 1783 dòng trong 1 file | 3-4 files, 200-400 dòng mỗi cái |
| Khó maintain | Dễ find & fix |
| Khó test | Test từng tab |
| State loạn xạ | Props flow rõ ràng |

## 📚 File Status

### ✅ Đã Tạo
- `ProductTab.jsx` - Product listing & search
- `CartTab.jsx` - Cart management  
- `OrdersTab.jsx` - Order tracking
- `COMPONENT_BREAKDOWN_GUIDE.md` - Hướng dẫn chi tiết

### 📋 TODO: PetsTab.jsx
```javascript
const PetsTab = ({
  pets = [],
  loading = false,
  onAddPet,
  onDeletePet,
  onEditPet,
}) => {
  // Pet list
  // Add pet form
  // Delete button
};
```

### 📋 TODO: ProfileTab.jsx
```javascript
const ProfileTab = ({
  user = {},
  onUpdateProfile,
  loading = false,
}) => {
  // User info form
  // Edit & save buttons
  // Validation
};
```

### 📋 TODO: EmployeePage Components
- ProductsTab.jsx
- MedicalRecordsTab.jsx
- DeliveryTab.jsx

## 🚀 Cách Sử Dụng Components

### Import
```javascript
import { ProductTab, CartTab, OrdersTab } from './components';

// Hoặc
import ProductTab from './components/ProductTab';
```

### Props Flow
```javascript
const CustomerPage = () => {
  const [products, setProducts] = useState([]);
  
  const handleAddToCart = (productId) => {
    // Logic
  };
  
  return (
    <>
      {activeTab === 'products' && (
        <ProductTab
          products={products}
          categories={categories}
          loading={loading}
          onAddToCart={handleAddToCart}
          onBuyNow={handleBuyNow}
          onSearch={handleSearch}
          onCategorySelect={handleCategorySelect}
        />
      )}
    </>
  );
};
```

## 💡 Pattern & Best Practices

✅ **One Tab = One Component**  
✅ **Props over State (in tabs)**  
✅ **Logic stays in Parent (CustomerPage)**  
✅ **Callbacks for Actions**  
✅ **Barrel Export (index.js)**  

## ⚠️ Things to Remember

1. **Don't** move state to tab components
2. **Do** keep logic in main page
3. **Do** use callbacks for actions
4. **Don't** create circular dependencies
5. **Do** extract reusable sub-components

## 📞 Example: Full Integration

```javascript
// pages/customer/CustomerPage.jsx
import { ProductTab, CartTab, OrdersTab } from './components';

const CustomerPage = () => {
  // ===== STATE =====
  const [products, setProducts] = useState([]);
  const [cartItems, setCartItems] = useState([]);
  const [orders, setOrders] = useState([]);
  const [activeTab, setActiveTab] = useState('products');
  
  // ===== HANDLERS =====
  const handleAddToCart = async (productId) => {
    try {
      const response = await cartAPI.addToCart({
        userId: user.userId,
        productId,
        quantity: 1,
      });
      setCartItems(response.data);
    } catch (error) {
      console.error(error);
    }
  };
  
  // ===== RENDER =====
  return (
    <div>
      {/* Tab Buttons */}
      <div className="tabs">
        <button onClick={() => setActiveTab('products')}>Sản Phẩm</button>
        <button onClick={() => setActiveTab('cart')}>Giỏ</button>
        <button onClick={() => setActiveTab('orders')}>Đơn Hàng</button>
      </div>
      
      {/* Tab Content */}
      {activeTab === 'products' && (
        <ProductTab
          products={products}
          loading={loading}
          onAddToCart={handleAddToCart}
        />
      )}
      
      {activeTab === 'cart' && (
        <CartTab
          cartItems={cartItems}
          onCheckout={handleCheckout}
        />
      )}
      
      {activeTab === 'orders' && (
        <OrdersTab
          orders={orders}
          onCancelOrder={handleCancelOrder}
        />
      )}
    </div>
  );
};
```

## 🎯 Next Steps

1. **Hoàn thành PetsTab.jsx & ProfileTab.jsx** (20 phút)
2. **Hoàn thành EmployeePage components** (40 phút)
3. **Update imports trong main pages** (30 phút)
4. **Test & debug** (20 phút)

---

**Total Refactor Time**: ~2 giờ  
**Complexity**: Low - Mid  
**Risk**: Very Low (không thay đổi logic)
