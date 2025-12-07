# 📚 Component Breakdown Guide - CustomerPage

## 🎯 Chiến Lược Tách Components

Thay vì 1 file 1783 dòng, ta sẽ tách thành:

```
pages/customer/
├── CustomerPage.jsx           (Main container - chỉ logic & state)
├── components/
│   ├── ProductTab.jsx         (Products listing & filtering)
│   ├── CartTab.jsx            (Cart management)
│   ├── OrdersTab.jsx          (Order tracking & history)
│   ├── PetsTab.jsx            (Pet management)
│   ├── ProfileTab.jsx         (User profile)
│   ├── modals/
│   │   ├── CheckoutModal.jsx
│   │   ├── CreatePetModal.jsx
│   │   └── OrderTrackingModal.jsx
│   └── index.js
└── index.js
```

## 📋 Chi Tiết Từng Component

### ProductTab.jsx
**Chứa**: Products grid, search, filtering, pagination
**Props**: 
- `products` - Danh sách sản phẩm
- `categories` - Danh sách category
- `loading` - Loading state
- `onAddToCart` - Callback add to cart
- `onBuyNow` - Callback buy now

```javascript
const ProductTab = ({ products, categories, loading, onAddToCart, onBuyNow }) => {
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  
  return (
    // Search & Filter UI
    // Products Grid
  );
};
```

### CartTab.jsx
**Chứa**: Cart items, quantity controls, checkout
**Props**:
- `cartItems` - Items in cart
- `cartData` - Cart totals
- `loading` - Loading state
- `onUpdateItem` - Update quantity
- `onRemoveItem` - Remove item
- `onCheckout` - Checkout action

```javascript
const CartTab = ({ cartItems, cartData, loading, onUpdateItem, onRemoveItem, onCheckout }) => {
  return (
    // Cart items table/list
    // Quantity controls
    // Checkout button
  );
};
```

### OrdersTab.jsx
**Chứa**: Order tracking, order history, order details
**Props**:
- `orders` - User orders
- `loading` - Loading state
- `onCancelOrder` - Cancel order
- `onTrackOrder` - View tracking

```javascript
const OrdersTab = ({ orders, loading, onCancelOrder, onTrackOrder }) => {
  return (
    // Orders list
    // Status badges
    // Action buttons
  );
};
```

### PetsTab.jsx
**Chứa**: Pet list, add pet form
**Props**:
- `pets` - User pets
- `loading` - Loading state
- `onAddPet` - Add pet callback
- `onDeletePet` - Delete pet callback

```javascript
const PetsTab = ({ pets, loading, onAddPet, onDeletePet }) => {
  return (
    // Pets list
    // Add pet button
  );
};
```

### ProfileTab.jsx
**Chứa**: User information, edit profile
**Props**:
- `user` - User data
- `onUpdateProfile` - Save profile

```javascript
const ProfileTab = ({ user, onUpdateProfile }) => {
  return (
    // Profile form fields
    // Edit & save buttons
  );
};
```

## 🔄 Main CustomerPage Structure

Sau tách, CustomerPage sẽ dùng render như:

```javascript
const CustomerPage = () => {
  const { user, isAuthenticated } = useAuth();
  const [activeTab, setActiveTab] = useState('products');
  
  // ===== ALL STATE MANAGEMENT =====
  const [products, setProducts] = useState([]);
  const [cartItems, setCartItems] = useState([]);
  const [orders, setOrders] = useState([]);
  const [pets, setPets] = useState([]);
  // ... etc
  
  // ===== ALL HANDLERS =====
  const handleAddToCart = async (productId) => { ... };
  const handleBuyNow = (product) => { ... };
  const handleCheckout = async (paymentMethod) => { ... };
  // ... etc
  
  // ===== RENDER =====
  return (
    <div>
      <Header />
      <div className="container">
        {/* Tab Navigation */}
        <div className="tabs">
          <button onClick={() => setActiveTab('products')}>Sản Phẩm</button>
          <button onClick={() => setActiveTab('cart')}>Giỏ Hàng</button>
          <button onClick={() => setActiveTab('orders')}>Đơn Hàng</button>
          <button onClick={() => setActiveTab('pets')}>Thú Cưng</button>
          <button onClick={() => setActiveTab('profile')}>Hồ Sơ</button>
        </div>
        
        {/* Tab Content */}
        {activeTab === 'products' && (
          <ProductTab
            products={products}
            categories={categories}
            loading={loading}
            onAddToCart={handleAddToCart}
            onBuyNow={handleBuyNow}
          />
        )}
        
        {activeTab === 'cart' && (
          <CartTab
            cartItems={cartItems}
            cartData={cartData}
            loading={cartLoading}
            onUpdateItem={handleUpdateCartItem}
            onRemoveItem={handleDeleteCartItem}
            onCheckout={handleCheckout}
          />
        )}
        
        {activeTab === 'orders' && (
          <OrdersTab
            orders={orders}
            loading={ordersLoading}
            onCancelOrder={handleCancelOrder}
          />
        )}
        
        {activeTab === 'pets' && (
          <PetsTab
            pets={pets}
            loading={petsLoading}
            onAddPet={handleCreatePet}
            onDeletePet={handleDeletePet}
          />
        )}
        
        {activeTab === 'profile' && (
          <ProfileTab
            user={user}
            onUpdateProfile={handleUpdateProfile}
          />
        )}
        
        {/* Shared Modals */}
        <CheckoutModal {...checkoutProps} />
        <CreatePetModal {...petModalProps} />
      </div>
      <Footer />
    </div>
  );
};
```

## 🔧 Migration Steps

### Step 1: Tạo ProductTab.jsx
Cắt từ CustomerPage:
- Search form
- Filter by category
- Products grid
- Pagination

### Step 2: Tạo CartTab.jsx
Cắt từ CustomerPage:
- Cart items display
- Quantity controls
- Checkout section

### Step 3: Tạo OrdersTab.jsx
Cắt từ CustomerPage:
- Order list
- Tracking
- Cancel order

### Step 4: Tạo PetsTab.jsx
Cắt từ CustomerPage:
- Pet list
- Add pet form

### Step 5: Tạo ProfileTab.jsx
Cắt từ CustomerPage:
- User info
- Edit form

### Step 6: Extract Modals
Tạo `modals/` folder:
- CheckoutModal.jsx
- CreatePetModal.jsx
- OrderTrackingModal.jsx

### Step 7: Update Imports
Main CustomerPage import tất cả components

## 💡 Benefits

✅ **Dễ bảo trì** - Mỗi file 200-300 dòng  
✅ **Dễ test** - Test từng tab riêng  
✅ **Dễ reuse** - Components có thể dùng ở chỗ khác  
✅ **Dễ collaborate** - Nhiều người work cùng lúc  
✅ **Dễ debug** - Props flow rõ ràng  

## ⚠️ Lưu Ý

- Keep state logic in main CustomerPage.jsx
- Tab components chỉ nhận props
- Modals extract để tái sử dụng
- useCallback để optimize callbacks

## 📚 Example: ProductTab.jsx

```javascript
import { useState } from 'react';
import { Card, Button, Input, ProductImage } from '@/components';

const ProductTab = ({ 
  products, 
  categories, 
  loading, 
  onAddToCart, 
  onBuyNow 
}) => {
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    // Search logic
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <Card className="mb-6">
        <form onSubmit={handleSearch}>
          <Input 
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="Tìm kiếm..."
          />
          <Button>Tìm Kiếm</Button>
        </form>
      </Card>

      <div className="grid grid-cols-4 gap-4">
        {products.map(product => (
          <Card key={product.id}>
            <ProductImage productId={product.id} />
            <h3>{product.name}</h3>
            <Button onClick={() => onAddToCart(product.id)}>
              Thêm vào giỏ
            </Button>
            <Button onClick={() => onBuyNow(product)}>
              Mua ngay
            </Button>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default ProductTab;
```

## 🎯 Similar Pattern untuk EmployeePage

Có thể tách EmployeePage thành:
```
pages/employee/
├── EmployeePage.jsx
├── components/
│   ├── ProductsTab.jsx
│   ├── MedicalRecordsTab.jsx
│   ├── DeliveryTab.jsx
│   └── index.js
└── index.js
```

---

**Status**: Ready to Implement  
**Complexity**: Medium  
**Time to Refactor**: ~2-3 hours
