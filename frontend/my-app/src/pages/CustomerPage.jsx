import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { cartAPI, orderAPI, petAPI, customerProductAPI, paymentAPI, customerAppointmentAPI } from '../services/api';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';
import ProductImage from '../components/ProductImage';

const CustomerPage = () => {
  const { user, isAuthenticated } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const activeTab = searchParams.get('tab') || 'products'; // 'products', 'cart', 'pets', 'profile', 'appointments'
  
  // Cart states
  const [cartItems, setCartItems] = useState([]);
  const [cartLoading, setCartLoading] = useState(false);
  const [cartData, setCartData] = useState({
    cartId: null,
    totalPrice: 0,
  });
  
  // Pets states
  const [pets, setPets] = useState([]);
  const [petsLoading, setPetsLoading] = useState(false);
  const [showCreatePetModal, setShowCreatePetModal] = useState(false);
  const [petFormData, setPetFormData] = useState({
    petCode: '',
    petName: '',
    petType: 'DOG',
    breed: '',
    age: '',
    weight: '',
    ownerPhoneNumber: user?.phoneNumber || '',
  });
  
  // Profile states
  const [profileData, setProfileData] = useState({
    fullName: user?.fullName || '',
    phoneNumber: user?.phoneNumber || '',
    gmail: user?.gmail || '',
    address: '',
  });
  
  // Order form states
  const [showPlaceOrderModal, setShowPlaceOrderModal] = useState(false);
  const fixedShopAddress = "01 Đ. Võ Văn Ngân,P. Linh Chiểu,TP Thủ Đức, Thành phố Hồ Chí Minh";
  const [orderFormData, setOrderFormData] = useState({
    paymentMethod: 'CASH',
    fromAddress: fixedShopAddress, // Địa chỉ người gửi (cửa hàng) - fixed
    toAddress: '', // Địa chỉ người nhận (khách hàng)
    phoneNumber: user?.phoneNumber || '',
    note: '',
    discountCode: '',
  });
  
  // Buy Now states
  const [showBuyNowModal, setShowBuyNowModal] = useState(false);
  const [buyNowProduct, setBuyNowProduct] = useState(null);
  const [buyNowQuantity, setBuyNowQuantity] = useState(1);
  const [buyNowFormData, setBuyNowFormData] = useState({
    paymentMethod: 'CASH',
    shippingAddress: '',
    phoneNumber: user?.phoneNumber || '',
    note: '',
    discountCode: '',
  });
  
  // Order confirmation states
  const [showOrderConfirmationModal, setShowOrderConfirmationModal] = useState(false);
  const [orderConfirmationData, setOrderConfirmationData] = useState(null);
  
  // Checkout states for MoMo payment
  const [showCheckoutModal, setShowCheckoutModal] = useState(false);
  const [checkoutData, setCheckoutData] = useState(null);
  const [momoPayUrl, setMomoPayUrl] = useState(null);

  // Products states
  const [products, setProducts] = useState([]);
  const [productsLoading, setProductsLoading] = useState(false);
  const [productPage, setProductPage] = useState(0);
  const [productTotalPages, setProductTotalPages] = useState(1);
  const [productPageSize] = useState(10);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [categories, setCategories] = useState([]);

  // Appointments states
  const [appointments, setAppointments] = useState([]);
  const [appointmentLoading, setAppointmentLoading] = useState(false);
  const [appointmentPage, setAppointmentPage] = useState(0);
  const [appointmentTotalPages, setAppointmentTotalPages] = useState(1);

  // Redirect to products tab if no tab specified
  useEffect(() => {
    if (isAuthenticated && user?.role === 'CUSTOMER' && !searchParams.get('tab')) {
      setSearchParams({ tab: 'products' });
    }
  }, [isAuthenticated, user, searchParams, setSearchParams]);

  useEffect(() => {
    if (isAuthenticated && user?.role === 'CUSTOMER') {
      loadCart();
      loadPets();
      loadProfile();
      if (activeTab === 'products') {
        loadProducts();
      }
    }
  }, [isAuthenticated, user, activeTab]);

  useEffect(() => {
    if (isAuthenticated && user?.role === 'CUSTOMER' && activeTab === 'cart') {
      loadCart();
    }
  }, [activeTab]);

  useEffect(() => {
    if (isAuthenticated && user?.role === 'CUSTOMER' && activeTab === 'products') {
      loadProducts();
    }
  }, [productPage, searchKeyword, selectedCategory]);

  useEffect(() => {
    if (isAuthenticated && user?.role === 'CUSTOMER' && activeTab === 'appointments') {
      const userId = user?.userId || localStorage.getItem('userId');
      if (userId) {
        loadAppointments(userId);
      }
    }
  }, [isAuthenticated, user, activeTab, appointmentPage]);

  // Load cart
  const loadCart = async () => {
    // Get userId from localStorage if not in user object
    const userId = user?.userId || localStorage.getItem('userId');
    if (!userId) {
      console.warn('No userId found, cannot load cart');
      return;
    }
    
    try {
      setCartLoading(true);
      const response = await cartAPI.getCartItems(userId);
      console.log('Cart response:', response.data);
      
      if (response.data) {
        // Handle paginated response
        const items = response.data.content || response.data || [];
        console.log('Cart items from response:', items);
        
        const formattedItems = items.map(item => {
          // cartItemId should be directly in the item object from response
          const cartItemId = item.cartItemId;
          
          if (!cartItemId) {
            console.warn('Item missing cartItemId:', item);
          }
          
          return {
            cartItemId: cartItemId,
            productId: item.productId || item.product?.id,
            productName: item.productName || item.product?.name || item.name,
            price: item.price || item.product?.price || 0,
            quantity: item.quantity || 0,
            thumbnailUrl: item.thumbnailUrl || item.product?.thumbnailUrl,
            stockQuantity: item.stockQuantity || item.product?.stockQuantity || 0,
          };
        });
        
        console.log('Formatted cart items:', formattedItems);
        setCartItems(formattedItems);
        
        // Save cartItemIds to localStorage
        const cartItemIds = formattedItems
          .map(item => item.cartItemId)
          .filter(Boolean);
        localStorage.setItem('cartItemIds', JSON.stringify(cartItemIds));
        console.log('Saved cartItemIds to localStorage:', cartItemIds);
        
        // Calculate total price
        const totalPrice = formattedItems.reduce((sum, item) => {
          return sum + (item.price * item.quantity);
        }, 0);
        setCartData(prev => ({ ...prev, totalPrice }));
      }
    } catch (error) {
      console.error('Error loading cart:', error);
      setCartItems([]);
      localStorage.removeItem('cartItemIds');
    } finally {
      setCartLoading(false);
    }
  };

  // Handle cart response from addToCart API
  const handleCartResponse = (cartResponse) => {
    if (cartResponse) {
      setCartData({
        cartId: cartResponse.cartId || null,
        totalPrice: cartResponse.totalPrice || 0,
      });
      // Transform products array to cart items format
      const items = (cartResponse.products || []).map(product => ({
        ...product,
        cartItemId: product.cartItemId || null, // May come from other API
        name: product.productName || product.name,
      }));
      setCartItems(items);
    }
  };

  // Load pets
  const loadPets = async () => {
    try {
      setPetsLoading(true);
      // TODO: Implement API call to get user's pets
      setPets([]); // Placeholder
    } catch (error) {
      console.error('Error loading pets:', error);
    } finally {
      setPetsLoading(false);
    }
  };

  // Load profile
  const loadProfile = () => {
    if (user) {
      setProfileData({
        fullName: user.fullName || '',
        phoneNumber: user.phoneNumber || '',
        gmail: user.gmail || '',
        address: user.address || '',
      });
    }
  };

  // Cart functions
  const handleUpdateCartItem = async (cartItemId, quantity) => {
    if (!isAuthenticated) {
      alert('Vui lòng đăng nhập');
      return;
    }

    // Try to get cartItemId from localStorage if not provided
    if (!cartItemId) {
      const savedCartItemIds = JSON.parse(localStorage.getItem('cartItemIds') || '[]');
      if (savedCartItemIds.length > 0) {
        // Try to find the cartItemId from current cart items
        const currentItem = cartItems.find(item => item.cartItemId);
        if (currentItem?.cartItemId) {
          cartItemId = currentItem.cartItemId;
        } else {
          alert('Không tìm thấy cart item ID. Vui lòng tải lại trang.');
          loadCart();
          return;
        }
      } else {
        alert('Không tìm thấy cart item ID. Vui lòng tải lại trang.');
        loadCart();
        return;
      }
    }

    if (quantity <= 0) {
      alert('Số lượng phải lớn hơn 0');
      return;
    }

    try {
      const response = await cartAPI.updateCartItem(cartItemId, quantity);
      if (response.data) {
        // Reload cart to get updated data
        loadCart();
      }
    } catch (error) {
      console.error('Error updating cart item:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi cập nhật giỏ hàng');
      }
    }
  };

  const handleRemoveFromCart = async (cartItemId) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
      return;
    }

    // Try to get cartItemId from localStorage if not provided
    if (!cartItemId) {
      const savedCartItemIds = JSON.parse(localStorage.getItem('cartItemIds') || '[]');
      if (savedCartItemIds.length > 0) {
        // Try to find the cartItemId from current cart items
        const currentItem = cartItems.find(item => item.cartItemId);
        if (currentItem?.cartItemId) {
          cartItemId = currentItem.cartItemId;
        } else {
          alert('Không tìm thấy cart item ID. Vui lòng tải lại trang.');
          loadCart();
          return;
        }
      } else {
        alert('Không tìm thấy cart item ID. Vui lòng tải lại trang.');
        loadCart();
        return;
      }
    }

    try {
      await cartAPI.deleteCartItem(cartItemId);
      // Update localStorage
      const savedCartItemIds = JSON.parse(localStorage.getItem('cartItemIds') || '[]');
      const updatedCartItemIds = savedCartItemIds.filter(id => id !== cartItemId);
      localStorage.setItem('cartItemIds', JSON.stringify(updatedCartItemIds));
      // Reload cart to update total and get fresh data
      loadCart();
      alert('Đã xóa sản phẩm khỏi giỏ hàng!');
    } catch (error) {
      console.error('Error removing from cart:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi xóa sản phẩm');
      }
    }
  };

  const handlePlaceOrder = async (e) => {
    e.preventDefault();
    
    if (cartItems.length === 0) {
      alert('Giỏ hàng của bạn đang trống');
      return;
    }

    if (!orderFormData.toAddress.trim()) {
      alert('Vui lòng nhập địa chỉ giao hàng');
      return;
    }

    if (!orderFormData.phoneNumber.trim()) {
      alert('Vui lòng nhập số điện thoại');
      return;
    }

    try {
      // Get userId from localStorage
      const userId = localStorage.getItem('userId') || user?.userId;
      if (!userId) {
        alert('Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.');
        return;
      }

      const cartItemIds = cartItems.map(item => item.cartItemId).filter(Boolean);
      if (cartItemIds.length === 0) {
        alert('Không có sản phẩm nào trong giỏ hàng');
        return;
      }

      // Save original payment method to check if MoMo is selected
      const originalPaymentMethod = orderFormData.paymentMethod;
      
      // Convert payment method: MOMO -> BANKING, CASH -> CASH
      let paymentMethod = orderFormData.paymentMethod;
      if (paymentMethod === 'MOMO') {
        paymentMethod = 'BANKING';
      } else if (paymentMethod === 'CASH') {
        paymentMethod = 'CASH';
      }

      const orderData = {
        cartItemIds: cartItemIds,
        paymentMethod: paymentMethod,
        fromAddress: fixedShopAddress,
        toAddress: orderFormData.toAddress.trim(),
        phoneNumber: orderFormData.phoneNumber.trim(),
        note: orderFormData.note.trim() || '',
        serviceType: 2, // 2: Hàng nhẹ, 5: Hàng nặng
      };

      // Add discount code if provided
      if (orderFormData.discountCode.trim()) {
        orderData.discountCode = orderFormData.discountCode.trim();
      }

      const response = await orderAPI.placeOrder(userId, orderData);
      
      // If payment method is MoMo, show checkout page first
      if (response.data && originalPaymentMethod === 'MOMO') {
        // Close place order modal and show checkout modal
        setShowPlaceOrderModal(false);
        setCheckoutData(response.data);
        setShowCheckoutModal(true);
      } else if (response.data) {
        // For CASH payment, show confirmation modal as usual
        setOrderConfirmationData(response.data);
        setShowPlaceOrderModal(false);
        setShowOrderConfirmationModal(true);
      }
    } catch (error) {
      console.error('Error placing order:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi đặt hàng');
      }
    }
  };

  // Handle MoMo payment from checkout
  const handleMomoPayment = async () => {
    if (!checkoutData) {
      alert('Không tìm thấy thông tin đơn hàng. Vui lòng thử lại.');
      return;
    }

    // Use orderNumber first (format: ORD-xxx), fallback to orderId
    const orderId = checkoutData.orderNumber || checkoutData.orderId;
    const totalPrice = checkoutData.totalPrice;
    
    if (!orderId) {
      alert('Không tìm thấy orderId từ response. Vui lòng thử lại.');
      return;
    }
    
    if (!totalPrice || totalPrice <= 0) {
      alert('Không tìm thấy tổng tiền từ response. Vui lòng thử lại.');
      return;
    }

    try {
      // Create MoMo payment
      const momoResponse = await paymentAPI.createMomoPayment(orderId, totalPrice);
      
      if (momoResponse.data && momoResponse.data.payUrl) {
        // Close checkout modal and redirect to MoMo payment URL
        setShowCheckoutModal(false);
        window.location.href = momoResponse.data.payUrl;
      } else {
        alert('Không thể tạo link thanh toán MoMo. Vui lòng thử lại.');
      }
    } catch (momoError) {
      console.error('Error creating MoMo payment:', momoError);
      if (momoError.response?.data?.message) {
        alert(`Lỗi khi tạo thanh toán MoMo: ${momoError.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo thanh toán MoMo');
      }
    }
  };

  // Pet functions
  const handleCreatePet = async (e) => {
    e.preventDefault();
    
    if (!petFormData.petCode.trim()) {
      alert('Vui lòng nhập mã thú cưng');
      return;
    }
    if (!petFormData.petName.trim()) {
      alert('Vui lòng nhập tên thú cưng');
      return;
    }
    if (!petFormData.ownerPhoneNumber.trim()) {
      alert('Vui lòng nhập số điện thoại chủ sở hữu');
      return;
    }

    try {
      setPetsLoading(true);
      await petAPI.create(petFormData);
      alert('Tạo thú cưng thành công!');
      setShowCreatePetModal(false);
      setPetFormData({
        petCode: '',
        petName: '',
        petType: 'DOG',
        breed: '',
        age: '',
        weight: '',
        ownerPhoneNumber: user?.phoneNumber || '',
      });
      loadPets();
    } catch (error) {
      console.error('Error creating pet:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo thú cưng');
      }
    } finally {
      setPetsLoading(false);
    }
  };

  // Load products
  const loadProducts = async () => {
    try {
      setProductsLoading(true);
      let response;
      
      if (searchKeyword.trim()) {
        response = await customerProductAPI.search(searchKeyword.trim(), productPage, productPageSize);
      } else if (selectedCategory) {
        response = await customerProductAPI.getByCategory(selectedCategory, productPage, productPageSize);
      } else {
        response = await customerProductAPI.getAll(productPage, productPageSize);
      }

      console.log('Products response:', response.data);

      if (response.data) {
        // Handle response structure: { content: [...], page: {...} }
        const productsData = response.data.content || [];
        setProducts(productsData);
        
        // Handle pagination info
        if (response.data.page) {
          setProductTotalPages(response.data.page.totalPages || 1);
        } else if (response.data.totalPages !== undefined) {
          setProductTotalPages(response.data.totalPages);
        } else {
          setProductTotalPages(1);
        }
        
        // Extract unique categories from products
        const uniqueCategories = [...new Set(productsData.map(p => p.categoryName).filter(Boolean))];
        setCategories(prev => {
          const combined = [...new Set([...prev, ...uniqueCategories])];
          return combined;
        });
      } else {
        setProducts([]);
        setProductTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading products:', error);
      console.error('Error response:', error.response);
      console.error('Error message:', error.message);
      console.error('Error status:', error.response?.status);
      console.error('Error data:', error.response?.data);
      
      if (error.response?.status === 401) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
      } else if (error.response?.status === 403) {
        alert('Bạn không có quyền truy cập. Vui lòng đăng nhập với tài khoản phù hợp.');
      } else if (error.response?.status === 404) {
        alert('Không tìm thấy endpoint. Vui lòng kiểm tra lại API.');
      } else if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else if (error.message) {
        alert(`Lỗi: ${error.message}`);
      } else {
        alert('Có lỗi xảy ra khi tải danh sách sản phẩm. Vui lòng kiểm tra console để biết thêm chi tiết.');
      }
      
      // Set empty products on error
      setProducts([]);
      setProductTotalPages(0);
    } finally {
      setProductsLoading(false);
    }
  };

  // Load appointments
  const loadAppointments = async (userId) => {
    try {
      setAppointmentLoading(true);
      const response = await customerAppointmentAPI.getAppointments(userId, appointmentPage, 10);
      console.log('Appointments response:', response.data);
      
      if (response.data?.content) {
        setAppointments(response.data.content);
        
        if (response.data.page) {
          setAppointmentTotalPages(response.data.page.totalPages || 1);
        }
      } else {
        setAppointments([]);
        setAppointmentTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading appointments:', error);
      if (error.response?.status === 401) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
      } else if (error.response?.status === 403) {
        alert('Bạn không có quyền truy cập trang này.');
      } else {
        alert('Có lỗi xảy ra khi tải danh sách lịch hẹn.');
      }
      setAppointments([]);
      setAppointmentTotalPages(1);
    } finally {
      setAppointmentLoading(false);
    }
  };

  // Handle add to cart from products
  const handleAddToCart = async (productId) => {
    if (!isAuthenticated) {
      alert('Vui lòng đăng nhập');
      return;
    }

    // Get userId from localStorage if not in user object
    const userId = user?.userId || localStorage.getItem('userId');
    if (!userId) {
      alert('Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.');
      return;
    }

    try {
      const response = await cartAPI.addToCart({
        userId: userId,
        productId: productId,
        quantity: 1,
      });

      if (response.data) {
        alert('Đã thêm sản phẩm vào giỏ hàng!');
        // Reload cart to get updated items with cartItemIds
        await loadCart();
        // Switch to cart tab
        setSearchParams({ tab: 'cart' });
      }
    } catch (error) {
      console.error('Error adding to cart:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi thêm vào giỏ hàng');
      }
    }
  };

  // Handle buy now
  const handleBuyNow = (product) => {
    setBuyNowProduct(product);
    setBuyNowQuantity(1);
      setBuyNowFormData({
        paymentMethod: 'CASH',
        shippingAddress: '',
        phoneNumber: user?.phoneNumber || '',
        note: '',
        discountCode: '',
      });
    setShowBuyNowModal(true);
  };

  const handleSubmitBuyNow = async (e) => {
    e.preventDefault();
    
    if (!buyNowProduct) return;

    if (!buyNowFormData.shippingAddress.trim()) {
      alert('Vui lòng nhập địa chỉ giao hàng');
      return;
    }

    if (!buyNowFormData.phoneNumber.trim()) {
      alert('Vui lòng nhập số điện thoại');
      return;
    }

    try {
      const orderData = {
        userId: user.userId,
        shippingAddress: buyNowFormData.shippingAddress.trim(),
        phoneNumber: buyNowFormData.phoneNumber.trim(),
        orderItems: [
          {
            productId: buyNowProduct.id,
            quantity: buyNowQuantity,
          }
        ],
        note: buyNowFormData.note.trim() || '',
        paymentMethod: buyNowFormData.paymentMethod,
      };

      // Add discount code if provided
      if (buyNowFormData.discountCode.trim()) {
        orderData.discountCode = buyNowFormData.discountCode.trim();
      }

      await orderAPI.buyNow(orderData);
      alert('Đặt hàng thành công!');
      setShowBuyNowModal(false);
      setBuyNowProduct(null);
    } catch (error) {
      console.error('Error placing buy now order:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi đặt hàng');
      }
    }
  };

  // Handle order confirmation
  const handleConfirmOrder = async () => {
    // Order is already placed, just close the modal and reload
    setShowOrderConfirmationModal(false);
    setOrderConfirmationData(null);
    setShowPlaceOrderModal(false);
    setOrderFormData({
      paymentMethod: 'CASH',
      fromAddress: fixedShopAddress,
      toAddress: '',
      phoneNumber: user?.phoneNumber || '',
      note: '',
      discountCode: '',
    });
    loadCart();
  };

  const handleCancelPlacedOrder = async () => {
    if (!orderConfirmationData?.orderNumber) {
      alert('Không tìm thấy số đơn hàng');
      return;
    }

    if (!window.confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) {
      return;
    }

    try {
      await orderAPI.cancelOrder(orderConfirmationData.orderNumber);
      alert('Đã hủy đơn hàng thành công!');
      setShowOrderConfirmationModal(false);
      setOrderConfirmationData(null);
      setShowPlaceOrderModal(false);
      setOrderFormData({
        paymentMethod: 'CASH',
        fromAddress: fixedShopAddress,
        toAddress: '',
        phoneNumber: user?.phoneNumber || '',
        note: '',
        discountCode: '',
      });
      loadCart();
    } catch (error) {
      console.error('Error canceling order:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi hủy đơn hàng');
      }
    }
  };

  // Handle search
  const handleSearch = (e) => {
    e.preventDefault();
    setProductPage(0);
    // useEffect will trigger loadProducts when productPage, searchKeyword, or selectedCategory changes
  };

  // Handle category filter
  const handleCategoryChange = (category) => {
    setSelectedCategory(category);
    setSearchKeyword('');
    setProductPage(0);
    // useEffect will trigger loadProducts when productPage, searchKeyword, or selectedCategory changes
  };

  // Calculate cart total
  const calculateCartTotal = () => {
    // Use totalPrice from cartData if available, otherwise calculate from items
    if (cartData.totalPrice > 0) {
      return cartData.totalPrice;
    }
    return cartItems.reduce((total, item) => {
      return total + (item.price || 0) * (item.quantity || 0);
    }, 0);
  };

  // Helper function to get product thumbnail URL
  const getProductThumbnailUrl = (productId) => {
    if (!productId) return null;
    const API_BASE_URL = 'http://localhost:8080/api';
    // Correct endpoint: /api/customers/products/{id}/thumbnail
    return `${API_BASE_URL}/customers/products/${productId}/thumbnail`;
  };

  if (!isAuthenticated || user?.role !== 'CUSTOMER') {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card>
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">Vui lòng đăng nhập với tài khoản Customer để truy cập trang này</p>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Trang Khách Hàng</h1>
        <div className="flex items-center gap-3">
          <div className="px-4 py-2 bg-primary-50 rounded-lg">
            <span className="text-sm text-gray-600">Xin chào, </span>
            <span className="font-semibold text-primary-700">{user?.fullName || user?.phoneNumber}</span>
          </div>
          {/* Cart Icon */}
          <button
            onClick={() => setSearchParams({ tab: 'cart' })}
            className="relative p-3 bg-primary-50 rounded-lg hover:bg-primary-100 transition cursor-pointer"
            title="Xem giỏ hàng"
          >
            <svg className="w-6 h-6 text-primary-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            {cartItems.length > 0 && (
              <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                {cartItems.length}
              </span>
            )}
          </button>
        </div>
      </div>

      {/* Products Tab */}
      {activeTab === 'products' && (
        <div>
          {/* Search and Filter Section */}
          <Card className="mb-6">
            <form onSubmit={handleSearch} className="mb-4">
              <div className="flex gap-4">
                <div className="flex-1">
                  <Input
                    type="text"
                    placeholder="Tìm kiếm sản phẩm..."
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                  />
                </div>
                <Button type="submit">
                  <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                  Tìm Kiếm
                </Button>
                {searchKeyword && (
                  <Button
                    variant="secondary"
                    onClick={() => {
                      setSearchKeyword('');
                      setProductPage(0);
                    }}
                  >
                    Xóa
                  </Button>
                )}
              </div>
            </form>

            {/* Category Filter */}
            <div className="flex flex-wrap gap-2">
              <button
                onClick={() => handleCategoryChange('')}
                className={`px-4 py-2 rounded-lg font-medium transition ${
                  !selectedCategory
                    ? 'bg-primary-600 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                Tất Cả
              </button>
              {categories.map((category) => (
                <button
                  key={category}
                  onClick={() => handleCategoryChange(category)}
                  className={`px-4 py-2 rounded-lg font-medium transition ${
                    selectedCategory === category
                      ? 'bg-primary-600 text-white'
                      : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                  }`}
                >
                  {category}
                </button>
              ))}
            </div>
          </Card>

          {/* Products Grid */}
          {productsLoading ? (
            <Card>
              <div className="text-center py-12">
                <p className="text-gray-500">Đang tải sản phẩm...</p>
              </div>
            </Card>
          ) : products.length === 0 ? (
            <Card>
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Không tìm thấy sản phẩm nào</p>
                <p className="text-gray-400 text-sm">
                  {searchKeyword || selectedCategory
                    ? 'Thử tìm kiếm với từ khóa khác hoặc chọn danh mục khác'
                    : 'Hiện tại không có sản phẩm nào trong hệ thống'}
                </p>
              </div>
            </Card>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-6">
                {products.map((product) => (
                  <Card key={product.id} className="hover:shadow-lg transition">
                    <div className="relative aspect-square bg-gray-200 rounded-t-lg overflow-hidden">
                      <ProductImage 
                        productId={product.id} 
                        productName={product.name}
                        size="medium"
                      />
                      {product.status !== 'ACTIVE' && (
                        <div className="absolute top-2 right-2 bg-red-500 text-white px-2 py-1 rounded text-xs font-semibold">
                          {product.status}
                        </div>
                      )}
                      {product.stockQuantity <= 0 && (
                        <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                          <span className="text-white font-bold">Hết Hàng</span>
                        </div>
                      )}
                    </div>
                    <div className="p-4">
                      <h3 className="font-bold text-lg mb-2 line-clamp-2">{product.name}</h3>
                      <div className="flex items-center gap-2 mb-2">
                        <span className="px-2 py-1 bg-primary-100 text-primary-700 rounded text-xs font-semibold">
                          {product.categoryName}
                        </span>
                        {product.petType && (
                          <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs">
                            {product.petType}
                          </span>
                        )}
                      </div>
                      <p className="text-primary-600 font-bold text-xl mb-3">
                        {product.price?.toLocaleString('vi-VN')} VNĐ
                      </p>
                      <p className="text-sm text-gray-600 mb-3">
                        Còn lại: <span className="font-semibold">{product.stockQuantity || 0}</span>
                      </p>
                      <div>
                        <Button
                          onClick={() => handleAddToCart(product.id)}
                          disabled={product.status !== 'ACTIVE' || product.stockQuantity <= 0}
                          className="w-full"
                        >
                          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                          </svg>
                          Thêm Vào Giỏ
                        </Button>
                      </div>
                    </div>
                  </Card>
                ))}
              </div>

              {/* Pagination */}
              {productTotalPages > 1 && (
                <div className="flex justify-center gap-2">
                  <Button
                    variant="secondary"
                    onClick={() => setProductPage((p) => Math.max(0, p - 1))}
                    disabled={productPage === 0}
                  >
                    Trước
                  </Button>
                  <span className="px-4 py-2 flex items-center">
                    Trang {productPage + 1} / {productTotalPages}
                  </span>
                  <Button
                    variant="secondary"
                    onClick={() => setProductPage((p) => Math.min(productTotalPages - 1, p + 1))}
                    disabled={productPage >= productTotalPages - 1}
                  >
                    Sau
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      )}

      {/* Cart Tab */}
      {activeTab === 'cart' && (
        <div>
          {cartLoading ? (
            <Card>
              <div className="text-center py-12">
                <p className="text-gray-500">Đang tải giỏ hàng...</p>
              </div>
            </Card>
          ) : cartItems.length === 0 ? (
            <Card>
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Giỏ hàng của bạn đang trống</p>
                <p className="text-gray-400 text-sm mb-6">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
                <Button onClick={() => setSearchParams({ tab: 'products' })}>
                  Xem Sản Phẩm
                </Button>
              </div>
            </Card>
          ) : (
            <>
              <Card className="mb-6">
                <h2 className="text-2xl font-bold mb-6">Giỏ Hàng Của Bạn</h2>
                <div className="space-y-4">
                  {cartItems.map((item, index) => (
                    <div
                      key={item.cartItemId || item.productId || `cart-item-${index}`}
                      className="relative flex items-center gap-4 p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition"
                    >
                      {!item.cartItemId && (
                        <div className="absolute top-2 right-2 bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded z-10">
                          Đang tải ID... Vui lòng tải lại
                        </div>
                      )}
                      <div className="w-20 h-20 bg-gray-200 rounded flex items-center justify-center relative">
                        {item.productId ? (
                          <img
                            src={getProductThumbnailUrl(item.productId)}
                            alt={item.productName || item.name}
                            className="w-full h-full object-cover rounded"
                            onError={(e) => {
                              e.target.style.display = 'none';
                              const noImageSpan = e.target.parentElement.querySelector('.no-image-placeholder');
                              if (noImageSpan) noImageSpan.style.display = 'flex';
                            }}
                          />
                        ) : null}
                        <span className="text-gray-400 text-xs no-image-placeholder" style={{ display: item.productId ? 'none' : 'flex' }}>No Image</span>
                      </div>
                      <div className="flex-1">
                        <h3 className="font-bold text-lg">{item.productName || item.name || 'N/A'}</h3>
                        {item.price && (
                          <p className="text-primary-600 font-semibold">
                            {item.price.toLocaleString('vi-VN')} VNĐ
                          </p>
                        )}
                      </div>
                      <div className="flex items-center gap-2">
                        <Button
                          variant="secondary"
                          onClick={() => {
                            if (item.cartItemId) {
                              handleUpdateCartItem(item.cartItemId, (item.quantity || 1) - 1);
                            } else {
                              alert('Không tìm thấy cart item ID. Vui lòng tải lại trang.');
                              loadCart();
                            }
                          }}
                          disabled={item.quantity <= 1 || !item.cartItemId}
                        >
                          -
                        </Button>
                        <span className="px-4 py-2 font-semibold">{item.quantity || 0}</span>
                        <Button
                          variant="secondary"
                          onClick={() => {
                            if (item.cartItemId) {
                              handleUpdateCartItem(item.cartItemId, (item.quantity || 1) + 1);
                            } else {
                              alert('Không tìm thấy cart item ID. Vui lòng tải lại trang.');
                              loadCart();
                            }
                          }}
                          disabled={!item.cartItemId}
                        >
                          +
                        </Button>
                      </div>
                      <div className="text-right">
                        <p className="font-bold text-lg">
                          {((item.price || 0) * (item.quantity || 0)).toLocaleString('vi-VN')} VNĐ
                        </p>
                        <Button
                          variant="danger"
                          className="mt-2 text-sm"
                          onClick={() => {
                            if (item.cartItemId) {
                              handleRemoveFromCart(item.cartItemId);
                            } else {
                              alert('Không tìm thấy cart item ID. Vui lòng tải lại trang.');
                              loadCart();
                            }
                          }}
                          disabled={!item.cartItemId}
                        >
                          Xóa
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </Card>

              <Card className="bg-gradient-to-r from-primary-50 to-primary-100">
                <div className="flex justify-between items-center">
                  <div>
                    <p className="text-gray-600">Tổng cộng:</p>
                    <p className="text-3xl font-bold text-primary-700">
                      {calculateCartTotal().toLocaleString('vi-VN')} VNĐ
                    </p>
                  </div>
                  <Button
                    onClick={() => setShowPlaceOrderModal(true)}
                    className="px-8 py-3 text-lg"
                  >
                    Đặt Hàng
                  </Button>
                </div>
              </Card>
            </>
          )}
        </div>
      )}


      {/* Pets Tab */}
      {activeTab === 'pets' && (
        <div>
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold">Thú Cưng Của Tôi</h2>
          </div>

          {petsLoading ? (
            <Card>
              <div className="text-center py-12">
                <p className="text-gray-500">Đang tải...</p>
              </div>
            </Card>
          ) : pets.length === 0 ? (
            <Card>
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Bạn chưa có thú cưng nào</p>
                <p className="text-gray-400 text-sm">Liên hệ nhân viên để thêm thú cưng vào hệ thống</p>
              </div>
            </Card>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {pets.map((pet) => (
                <Card key={pet.petCode} className="border-l-4 border-primary-500">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-xl font-bold">{pet.petName || 'N/A'}</h3>
                      <p className="text-sm text-gray-600">Mã: {pet.petCode || 'N/A'}</p>
                    </div>
                    <span className="px-2 py-1 bg-primary-100 text-primary-700 rounded text-xs">
                      {pet.petType || 'N/A'}
                    </span>
                  </div>
                  
                  <div className="space-y-2 text-sm">
                    <p><strong>Giống:</strong> {pet.breed || 'N/A'}</p>
                    <p><strong>Tuổi:</strong> {pet.age ? `${pet.age} tuổi` : 'N/A'}</p>
                    <p><strong>Cân nặng:</strong> {pet.weight ? `${pet.weight} kg` : 'N/A'}</p>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Profile Tab */}
      {activeTab === 'profile' && (
        <Card>
          <h2 className="text-2xl font-bold mb-6">Thông Tin Cá Nhân</h2>
          <div className="space-y-4 max-w-2xl">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Họ và Tên
              </label>
              <Input
                value={profileData.fullName}
                onChange={(e) => setProfileData({ ...profileData, fullName: e.target.value })}
                disabled
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Số Điện Thoại
              </label>
              <Input
                value={profileData.phoneNumber}
                onChange={(e) => setProfileData({ ...profileData, phoneNumber: e.target.value })}
                disabled
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <Input
                value={profileData.gmail}
                onChange={(e) => setProfileData({ ...profileData, gmail: e.target.value })}
                disabled
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Địa Chỉ
              </label>
              <Input
                value={profileData.address}
                onChange={(e) => setProfileData({ ...profileData, address: e.target.value })}
                disabled
              />
            </div>
            <div className="pt-4">
              <p className="text-sm text-gray-500">
                * Để thay đổi thông tin, vui lòng liên hệ với quản trị viên
              </p>
            </div>
          </div>
        </Card>
      )}

      {/* Appointments Tab */}
      {activeTab === 'appointments' && (
        <div>
          <h2 className="text-2xl font-bold mb-6">Lịch Hẹn Của Bạn</h2>
          
          {appointmentLoading ? (
            <Card>
              <div className="text-center py-12">
                <p className="text-gray-500">Đang tải lịch hẹn...</p>
              </div>
            </Card>
          ) : appointments.length === 0 ? (
            <Card>
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Bạn chưa có lịch hẹn nào</p>
                <p className="text-gray-400 text-sm">Liên hệ nhân viên để đặt lịch hẹn</p>
              </div>
            </Card>
          ) : (
            <div className="space-y-4">
              {appointments.map((appointment) => (
                <Card key={appointment.id} className="border-l-4 border-blue-500">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex-1">
                      <h3 className="text-xl font-bold mb-2">
                        Lịch Hẹn #{appointment.id?.substring(0, 8)}
                      </h3>
                      <div className="grid grid-cols-2 gap-4 text-sm">
                        <div>
                          <p className="text-gray-600">Thời gian hẹn:</p>
                          <p className="font-semibold">{new Date(appointment.appointmentTime).toLocaleString('vi-VN')}</p>
                        </div>
                        <div>
                          <p className="text-gray-600">Trạng thái:</p>
                          <p className={`font-semibold ${
                            appointment.status === 'SCHEDULED' ? 'text-blue-600' :
                            appointment.status === 'COMPLETED' ? 'text-green-600' :
                            appointment.status === 'CANCELLED' ? 'text-red-600' :
                            'text-gray-600'
                          }`}>
                            {appointment.status === 'SCHEDULED' ? 'Đã lên lịch' :
                             appointment.status === 'COMPLETED' ? 'Hoàn thành' :
                             appointment.status === 'CANCELLED' ? 'Đã hủy' :
                             appointment.status}
                          </p>
                        </div>
                        {appointment.acceptedBy && (
                          <div>
                            <p className="text-gray-600">Nhân viên tiếp nhận:</p>
                            <p className="font-semibold">{appointment.acceptedBy}</p>
                          </div>
                        )}
                        {appointment.description && (
                          <div>
                            <p className="text-gray-600">Mô tả:</p>
                            <p className="font-semibold">{appointment.description}</p>
                          </div>
                        )}
                        {appointment.customer && (
                          <>
                            <div>
                              <p className="text-gray-600">Khách hàng:</p>
                              <p className="font-semibold">{appointment.customer.fullName || 'N/A'}</p>
                            </div>
                            <div>
                              <p className="text-gray-600">Số điện thoại:</p>
                              <p className="font-semibold">{appointment.customer.phoneNumber || 'N/A'}</p>
                            </div>
                          </>
                        )}
                        {appointment.createdDate && (
                          <div>
                            <p className="text-gray-600">Ngày tạo:</p>
                            <p className="font-semibold">{new Date(appointment.createdDate).toLocaleString('vi-VN')}</p>
                          </div>
                        )}
                        {appointment.modifiedDate && (
                          <div>
                            <p className="text-gray-600">Ngày cập nhật:</p>
                            <p className="font-semibold">{new Date(appointment.modifiedDate).toLocaleString('vi-VN')}</p>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </Card>
              ))}

              {/* Pagination */}
              {appointmentTotalPages > 1 && (
                <div className="flex justify-center gap-2 mt-6">
                  <Button
                    variant="secondary"
                    onClick={() => {
                      setAppointmentPage((p) => Math.max(0, p - 1));
                    }}
                    disabled={appointmentPage === 0}
                  >
                    Trước
                  </Button>
                  <span className="px-4 py-2">
                    Trang {appointmentPage + 1} / {appointmentTotalPages}
                  </span>
                  <Button
                    variant="secondary"
                    onClick={() => {
                      setAppointmentPage((p) => Math.min(appointmentTotalPages - 1, p + 1));
                    }}
                    disabled={appointmentPage >= appointmentTotalPages - 1}
                  >
                    Sau
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* Place Order Modal */}
      <Modal
        isOpen={showPlaceOrderModal}
        onClose={() => {
          setShowPlaceOrderModal(false);
          setOrderFormData({
            paymentMethod: 'CASH',
            fromAddress: fixedShopAddress,
            toAddress: '',
            phoneNumber: user?.phoneNumber || '',
            note: '',
            discountCode: '',
          });
        }}
        title="Đặt Hàng"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowPlaceOrderModal(false);
                setOrderFormData({
                  paymentMethod: 'CASH',
                  fromAddress: fixedShopAddress,
                  toAddress: '',
                  phoneNumber: user?.phoneNumber || '',
                  note: '',
                  discountCode: '',
                });
              }}
            >
              Hủy
            </Button>
            <Button onClick={handlePlaceOrder}>
              Xác Nhận Đặt Hàng
            </Button>
          </>
        }
      >
        <form onSubmit={handlePlaceOrder}>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Phương Thức Thanh Toán <span className="text-red-500">*</span>
            </label>
            <select
              value={orderFormData.paymentMethod}
              onChange={(e) => setOrderFormData({ ...orderFormData, paymentMethod: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              required
            >
              <option value="CASH">Tiền mặt</option>
              <option value="MOMO">MoMo</option>
            </select>
            {orderFormData.paymentMethod === 'MOMO' && (
              <div className="mt-2 flex items-center gap-2 text-sm text-gray-600">
                <img 
                  src="https://cdn.haitrieu.com/wp-content/uploads/2022/10/Logo-MoMo-Square-1024x1024.png" 
                  alt="MoMo" 
                  className="w-6 h-6"
                />
                <span>Thanh toán qua MoMo</span>
              </div>
            )}
          </div>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Số Điện Thoại <span className="text-red-500">*</span>
            </label>
            <Input
              type="tel"
              value={orderFormData.phoneNumber}
              onChange={(e) => setOrderFormData({ ...orderFormData, phoneNumber: e.target.value })}
              required
              placeholder="Nhập số điện thoại..."
            />
          </div>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Địa Chỉ Người Gửi (Cửa Hàng) <span className="text-red-500">*</span>
            </label>
            <textarea
              value={orderFormData.fromAddress}
              readOnly
              className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-100 cursor-not-allowed"
              rows={3}
              placeholder="Địa chỉ cửa hàng (được gán cứng)"
            />
          </div>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Địa Chỉ Giao Hàng (Người Nhận) <span className="text-red-500">*</span>
            </label>
            <textarea
              value={orderFormData.toAddress}
              onChange={(e) => setOrderFormData({ ...orderFormData, toAddress: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              rows={3}
              required
              placeholder="Nhập địa chỉ giao hàng..."
            />
          </div>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Ghi Chú
            </label>
            <textarea
              value={orderFormData.note}
              onChange={(e) => setOrderFormData({ ...orderFormData, note: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              rows={2}
              placeholder="Ghi chú cho đơn hàng (tùy chọn)..."
            />
          </div>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Mã Giảm Giá (Tùy chọn)
            </label>
            <Input
              type="text"
              value={orderFormData.discountCode}
              onChange={(e) => setOrderFormData({ ...orderFormData, discountCode: e.target.value })}
              placeholder="Nhập mã giảm giá..."
            />
          </div>
          <div className="bg-primary-50 p-4 rounded-lg">
            <p className="text-sm text-gray-600 mb-1">Tổng tiền:</p>
            <p className="text-2xl font-bold text-primary-700">
              {calculateCartTotal().toLocaleString('vi-VN')} VNĐ
            </p>
          </div>
        </form>
      </Modal>


      {/* Buy Now Modal */}
      <Modal
        isOpen={showBuyNowModal}
        onClose={() => {
          setShowBuyNowModal(false);
          setBuyNowProduct(null);
      setBuyNowFormData({
        paymentMethod: 'CASH',
        shippingAddress: '',
        phoneNumber: user?.phoneNumber || '',
        note: '',
        discountCode: '',
      });
        }}
        title="Mua Ngay"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowBuyNowModal(false);
                setBuyNowProduct(null);
      setBuyNowFormData({
        paymentMethod: 'CASH',
        shippingAddress: '',
        phoneNumber: user?.phoneNumber || '',
        note: '',
        discountCode: '',
      });
              }}
            >
              Hủy
            </Button>
            <Button onClick={handleSubmitBuyNow}>
              Xác Nhận Đặt Hàng
            </Button>
          </>
        }
      >
        {buyNowProduct && (
          <form onSubmit={handleSubmitBuyNow}>
            <div className="mb-4 p-4 bg-gray-50 rounded-lg">
              <h3 className="font-bold text-lg mb-2">{buyNowProduct.name}</h3>
              <p className="text-primary-600 font-semibold text-xl mb-2">
                {buyNowProduct.price?.toLocaleString('vi-VN')} VNĐ
              </p>
              <div className="flex items-center gap-2">
                <label className="text-sm font-medium text-gray-700">Số lượng:</label>
                <div className="flex items-center gap-2">
                  <Button
                    type="button"
                    variant="secondary"
                    onClick={() => setBuyNowQuantity(Math.max(1, buyNowQuantity - 1))}
                  >
                    -
                  </Button>
                  <span className="px-4 py-2 font-semibold">{buyNowQuantity}</span>
                  <Button
                    type="button"
                    variant="secondary"
                    onClick={() => setBuyNowQuantity(Math.min(buyNowProduct.stockQuantity || 999, buyNowQuantity + 1))}
                  >
                    +
                  </Button>
                </div>
                <span className="text-sm text-gray-600">
                  (Còn lại: {buyNowProduct.stockQuantity || 0})
                </span>
              </div>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Phương Thức Thanh Toán <span className="text-red-500">*</span>
              </label>
              <select
                value={buyNowFormData.paymentMethod}
                onChange={(e) => setBuyNowFormData({ ...buyNowFormData, paymentMethod: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              >
                <option value="CASH">Tiền mặt</option>
                <option value="MOMO">MoMo</option>
              </select>
              {buyNowFormData.paymentMethod === 'MOMO' && (
                <div className="mt-2 flex items-center gap-2 text-sm text-gray-600">
                  <img 
                    src="https://cdn.haitrieu.com/wp-content/uploads/2022/10/Logo-MoMo-Square-1024x1024.png" 
                    alt="MoMo" 
                    className="w-6 h-6"
                  />
                  <span>Thanh toán qua MoMo</span>
                </div>
              )}
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Số Điện Thoại <span className="text-red-500">*</span>
              </label>
              <Input
                type="tel"
                value={buyNowFormData.phoneNumber}
                onChange={(e) => setBuyNowFormData({ ...buyNowFormData, phoneNumber: e.target.value })}
                required
                placeholder="Nhập số điện thoại..."
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Địa Chỉ Giao Hàng <span className="text-red-500">*</span>
              </label>
              <textarea
                value={buyNowFormData.shippingAddress}
                onChange={(e) => setBuyNowFormData({ ...buyNowFormData, shippingAddress: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                rows={3}
                required
                placeholder="Nhập địa chỉ giao hàng..."
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Ghi Chú
              </label>
              <textarea
                value={buyNowFormData.note}
                onChange={(e) => setBuyNowFormData({ ...buyNowFormData, note: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                rows={2}
                placeholder="Ghi chú cho đơn hàng (tùy chọn)..."
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Mã Giảm Giá (Tùy chọn)
              </label>
              <Input
                type="text"
                value={buyNowFormData.discountCode}
                onChange={(e) => setBuyNowFormData({ ...buyNowFormData, discountCode: e.target.value })}
                placeholder="Nhập mã giảm giá..."
              />
            </div>
            <div className="bg-primary-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600 mb-1">Tổng tiền:</p>
              <p className="text-2xl font-bold text-primary-700">
                {((buyNowProduct.price || 0) * buyNowQuantity).toLocaleString('vi-VN')} VNĐ
              </p>
            </div>
          </form>
        )}
      </Modal>

      {/* Checkout Modal for MoMo Payment */}
      <Modal
        isOpen={showCheckoutModal}
        onClose={() => {
          setShowCheckoutModal(false);
          setCheckoutData(null);
        }}
        title="Xác Nhận Thanh Toán"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowCheckoutModal(false);
                setCheckoutData(null);
              }}
            >
              Hủy
            </Button>
            <Button onClick={handleMomoPayment}>
              Thanh Toán MoMo
            </Button>
          </>
        }
      >
        {checkoutData && (
          <div className="space-y-4">
            <div className="bg-primary-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600 mb-1">Mã đơn hàng:</p>
              <p className="text-lg font-bold text-primary-700">{checkoutData.orderNumber || checkoutData.orderId}</p>
            </div>

            <div>
              <h3 className="font-semibold text-lg mb-3">Thông tin giao hàng:</h3>
              <div className="space-y-2 text-sm">
                <p><strong>Địa chỉ giao hàng:</strong> {checkoutData.shippingAddress || checkoutData.toAddress || 'N/A'}</p>
                <p><strong>Số điện thoại:</strong> {checkoutData.phoneNumber || 'N/A'}</p>
                {checkoutData.note && (
                  <p><strong>Ghi chú:</strong> {checkoutData.note}</p>
                )}
              </div>
            </div>

            <div>
              <h3 className="font-semibold text-lg mb-3">Sản phẩm:</h3>
              <div className="space-y-2">
                {checkoutData.orderItems && checkoutData.orderItems.length > 0 ? (
                  checkoutData.orderItems.map((item, index) => (
                    <div key={index} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                      <div className="flex-1">
                        <p className="font-medium">{item.name}</p>
                        <p className="text-sm text-gray-600">
                          {parseFloat(item.price || 0).toLocaleString('vi-VN')} VNĐ x {item.quantity}
                        </p>
                      </div>
                      <p className="font-semibold">
                        {(parseFloat(item.price || 0) * parseFloat(item.quantity || 0)).toLocaleString('vi-VN')} VNĐ
                      </p>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-500">Không có sản phẩm</p>
                )}
              </div>
            </div>

            <div className="border-t pt-4 space-y-2">
              <div className="flex justify-between text-sm">
                <span>Tổng tiền sản phẩm:</span>
                <span className="font-semibold">
                  {checkoutData.orderItems && checkoutData.orderItems.length > 0
                    ? checkoutData.orderItems.reduce((sum, item) => {
                        return sum + (parseFloat(item.price || 0) * parseFloat(item.quantity || 0));
                      }, 0).toLocaleString('vi-VN')
                    : '0'} VNĐ
                </span>
              </div>
              <div className="flex justify-between text-sm">
                <span>Phí vận chuyển:</span>
                <span className="font-semibold text-primary-600">
                  {parseFloat(checkoutData.shippingFee || 0).toLocaleString('vi-VN')} VNĐ
                </span>
              </div>
              {checkoutData.discount && (
                <div className="flex justify-between text-sm text-green-600">
                  <span>Giảm giá:</span>
                  <span className="font-semibold">
                    -{parseFloat(checkoutData.discount.amount || checkoutData.discount || 0).toLocaleString('vi-VN')} VNĐ
                  </span>
                </div>
              )}
              <div className="flex justify-between text-lg font-bold border-t pt-2">
                <span>Tổng cộng:</span>
                <span className="text-primary-700">
                  {parseFloat(checkoutData.totalPrice || 0).toLocaleString('vi-VN')} VNĐ
                </span>
              </div>
            </div>

            <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
              <div className="flex items-center gap-2 mb-2">
                <img 
                  src="https://cdn.haitrieu.com/wp-content/uploads/2022/10/Logo-MoMo-Square-1024x1024.png" 
                  alt="MoMo" 
                  className="w-8 h-8"
                />
                <p className="font-semibold text-blue-800">Thanh toán qua MoMo</p>
              </div>
              <p className="text-sm text-blue-600">
                Sau khi nhấn "Thanh Toán MoMo", bạn sẽ được chuyển đến trang thanh toán MoMo để hoàn tất giao dịch.
              </p>
            </div>
          </div>
        )}
      </Modal>

      {/* Order Confirmation Modal */}
      <Modal
        isOpen={showOrderConfirmationModal}
        onClose={() => {
          setShowOrderConfirmationModal(false);
          setOrderConfirmationData(null);
        }}
        title="Xác Nhận Đơn Hàng"
        footer={
          <>
            <Button
              variant="danger"
              onClick={handleCancelPlacedOrder}
            >
              Hủy Đơn Hàng
            </Button>
            <Button onClick={handleConfirmOrder}>
              Xác Nhận
            </Button>
          </>
        }
      >
        {orderConfirmationData && (
          <div className="space-y-4">
            <div className="bg-primary-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600 mb-1">Mã đơn hàng:</p>
              <p className="text-lg font-bold text-primary-700">{orderConfirmationData.orderNumber}</p>
            </div>

            <div>
              <h3 className="font-semibold text-lg mb-3">Thông tin giao hàng:</h3>
              <div className="space-y-2 text-sm">
                <p><strong>Địa chỉ giao hàng:</strong> {orderConfirmationData.shippingAddress || 'N/A'}</p>
                <p><strong>Số điện thoại:</strong> {orderConfirmationData.phoneNumber || 'N/A'}</p>
                {orderConfirmationData.note && (
                  <p><strong>Ghi chú:</strong> {orderConfirmationData.note}</p>
                )}
              </div>
            </div>

            <div>
              <h3 className="font-semibold text-lg mb-3">Sản phẩm:</h3>
              <div className="space-y-2">
                {orderConfirmationData.orderItems && orderConfirmationData.orderItems.length > 0 ? (
                  orderConfirmationData.orderItems.map((item, index) => (
                    <div key={index} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                      <div className="flex-1">
                        <p className="font-medium">{item.name}</p>
                        <p className="text-sm text-gray-600">
                          {parseFloat(item.price || 0).toLocaleString('vi-VN')} VNĐ x {item.quantity}
                        </p>
                      </div>
                      <p className="font-semibold">
                        {(parseFloat(item.price || 0) * parseFloat(item.quantity || 0)).toLocaleString('vi-VN')} VNĐ
                      </p>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-500">Không có sản phẩm</p>
                )}
              </div>
            </div>

            <div className="border-t pt-4 space-y-2">
              <div className="flex justify-between text-sm">
                <span>Tổng tiền sản phẩm:</span>
                <span className="font-semibold">
                  {orderConfirmationData.orderItems && orderConfirmationData.orderItems.length > 0
                    ? orderConfirmationData.orderItems.reduce((sum, item) => {
                        return sum + (parseFloat(item.price || 0) * parseFloat(item.quantity || 0));
                      }, 0).toLocaleString('vi-VN')
                    : '0'} VNĐ
                </span>
              </div>
              <div className="flex justify-between text-sm">
                <span>Phí vận chuyển:</span>
                <span className="font-semibold text-primary-600">
                  {parseFloat(orderConfirmationData.shippingFee || 0).toLocaleString('vi-VN')} VNĐ
                </span>
              </div>
              {orderConfirmationData.discount && (
                <div className="flex justify-between text-sm text-green-600">
                  <span>Giảm giá:</span>
                  <span className="font-semibold">
                    -{parseFloat(orderConfirmationData.discount).toLocaleString('vi-VN')} VNĐ
                  </span>
                </div>
              )}
              <div className="flex justify-between text-lg font-bold border-t pt-2">
                <span>Tổng cộng:</span>
                <span className="text-primary-700">
                  {parseFloat(orderConfirmationData.totalPrice || 0).toLocaleString('vi-VN')} VNĐ
                </span>
              </div>
            </div>

            <div className="bg-gray-50 p-3 rounded-lg">
              <p className="text-sm text-gray-600">
                <strong>Phương thức thanh toán:</strong> {orderConfirmationData.paymentMethod || 'N/A'}
              </p>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default CustomerPage;

