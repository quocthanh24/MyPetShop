import { useState, useEffect } from 'react';
import { employeeProductAPI, categoryAPI, medicalRecordAPI, employeeDeliveryAPI, employeeAppointmentAPI, employeeDiscountAPI } from '../services/api';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';
import { useAuth } from '../context/AuthContext';

// Component để lấy ảnh URL với authentication
const useProductImageUrl = (productId) => {
  const [imageUrl, setImageUrl] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (!productId) {
      setLoading(false);
      return;
    }

    const loadImage = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          setError(true);
          setLoading(false);
          return;
        }

        const response = await fetch(
          `http://localhost:8080/api/employees/products/${productId}/thumbnail`,
          {
            headers: {
              'Authorization': `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error('Failed to load image');
        }

        const blob = await response.blob();
        const blobUrl = URL.createObjectURL(blob);
        setImageUrl(blobUrl);
        setLoading(false);
      } catch (err) {
        console.error('Image load error for product:', productId, err);
        setError(true);
        setLoading(false);
      }
    };

    loadImage();

    return () => {
      if (imageUrl) {
        URL.revokeObjectURL(imageUrl);
      }
    };
  }, [productId]);

  return { imageUrl, loading, error };
};

// Component để hiển thị ảnh với error handling
const ProductImage = ({ thumbnailUrl, productId, productName }) => {
  const { imageUrl, loading, error } = useProductImageUrl(productId);

  if (!productId) {
    return (
      <div className="w-full h-full bg-gray-200 rounded flex items-center justify-center text-xs text-gray-400">
        No Image
      </div>
    );
  }

  if (loading) {
    return (
      <div className="w-full h-full bg-gray-200 rounded flex items-center justify-center text-xs text-gray-400">
        Loading...
      </div>
    );
  }

  if (error || !imageUrl) {
    return (
      <div className="w-full h-full bg-gray-200 rounded flex items-center justify-center text-xs text-gray-400">
        No Image
      </div>
    );
  }

  return (
    <div
      className="w-full h-full rounded bg-cover bg-center"
      style={{
        backgroundImage: `url(${imageUrl})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }}
    />
  );
};

const SENDER_ADDRESS = '01 Đ. Võ Văn Ngân,P. Linh Chiểu,TP Thủ Đức, Thành phố Hồ Chí Minh';

const EmployeePage = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  
  // Modal states
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  
  // Form states
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    stockQuantity: '',
    status: 'ACTIVE',
    petType: 'CAT',
    categoryName: '',
  });
  const [thumbnailFile, setThumbnailFile] = useState(null);
  const [imageFiles, setImageFiles] = useState([]);
  const [formLoading, setFormLoading] = useState(false);

  // Medical Record states
  const [activeTab, setActiveTab] = useState('products'); // 'products', 'medicalRecords', 'delivery', 'appointments', 'categories', or 'discounts'
  
  // Appointment states
  const { user } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [appointmentPage, setAppointmentPage] = useState(0);
  const [appointmentTotalPages, setAppointmentTotalPages] = useState(1);
  const [appointmentLoading, setAppointmentLoading] = useState(false);
  const [showCreateAppointmentModal, setShowCreateAppointmentModal] = useState(false);
  const [showUpdateAppointmentModal, setShowUpdateAppointmentModal] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState(null);
  const [appointmentFormData, setAppointmentFormData] = useState({
    customerPhoneNumber: '',
    employeeId: '',
    appointmentTime: '',
    status: 'SCHEDULED',
    description: '',
  });
  const [appointmentFormLoading, setAppointmentFormLoading] = useState(false);
  
  // Category states (for management tab)
  const [categoryPage, setCategoryPage] = useState(0);
  const [categoryTotalPages, setCategoryTotalPages] = useState(1);
  const [categoryLoading, setCategoryLoading] = useState(false);
  const [showCreateCategoryModal, setShowCreateCategoryModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [categoryFormData, setCategoryFormData] = useState({
    name: '',
    description: '',
  });
  const [categoryFormLoading, setCategoryFormLoading] = useState(false);
  
  // Discount states
  const [discounts, setDiscounts] = useState([]);
  const [discountLoading, setDiscountLoading] = useState(false);
  const [discountPage, setDiscountPage] = useState(0);
  const [discountTotalPages, setDiscountTotalPages] = useState(1);
  const [discountStatus, setDiscountStatus] = useState('ACTIVE');
  const [showCreateManualDiscountModal, setShowCreateManualDiscountModal] = useState(false);
  const [showCreateAutoDiscountModal, setShowCreateAutoDiscountModal] = useState(false);
  const [manualDiscountFormData, setManualDiscountFormData] = useState({
    discountCode: '',
    discountName: '',
    percent: '',
    startDate: '',
    endDate: '',
  });
  const [autoDiscountFormData, setAutoDiscountFormData] = useState({
    discountName: '',
    percent: '',
    startDate: '',
    endDate: '',
    totalLimit: '',
  });
  const [discountFormLoading, setDiscountFormLoading] = useState(false);
  
  // Delivery states
  const [deliveryOrders, setDeliveryOrders] = useState([]);
  const [deliveryLoading, setDeliveryLoading] = useState(false);
  const [deliveryPage, setDeliveryPage] = useState(0);
  const [deliveryTotalPages, setDeliveryTotalPages] = useState(1);
  const [showDeliveryModal, setShowDeliveryModal] = useState(false);
  const [selectedOrderForDelivery, setSelectedOrderForDelivery] = useState(null);
  const [deliveryFormData, setDeliveryFormData] = useState({
    senderName: '',
    senderPhone: '',
    height: '',
    weight: '',
    width: '',
    length: '',
    requiredNote: 'KHONGCHOXEMHANG',
  });
  const [deliveryFormLoading, setDeliveryFormLoading] = useState(false);
  
  const [showCreateMedicalRecordModal, setShowCreateMedicalRecordModal] = useState(false);
  const [showMedicalRecordDetailsModal, setShowMedicalRecordDetailsModal] = useState(false);
  const [showUpdateDetailModal, setShowUpdateDetailModal] = useState(false);
  const [selectedMedicalRecord, setSelectedMedicalRecord] = useState(null);
  const [medicalRecordDetails, setMedicalRecordDetails] = useState([]);
  const [editingDetail, setEditingDetail] = useState(null);
  const [medicalRecordFormData, setMedicalRecordFormData] = useState({
    employeePhoneNumber: '',
    pet: {
      name: '',
      type: 'dog',
      breed: '',
      age: '',
    },
    owner: {
      fullName: '',
      phoneNumber: '',
      address: '',
    },
    recordDetails: {
      healthCondition: '',
      medicalHistory: '',
      updatedDate: '',
      temperature: '',
      vaccines: '',
      diagnosisResult: '',
    },
  });
  const [detailFormData, setDetailFormData] = useState({
    healthCondition: '',
    medicalHistory: '',
    updatedDate: '',
    temperature: '',
    vaccines: '',
    diagnosisResult: '',
  });
  const [searchPhoneNumber, setSearchPhoneNumber] = useState('');
  const [medicalRecords, setMedicalRecords] = useState([]);
  const [medicalRecordLoading, setMedicalRecordLoading] = useState(false);

  useEffect(() => {
    loadProducts();
    loadCategories();
  }, [page, selectedCategory]);

  const loadProducts = async () => {
    try {
      setLoading(true);
      let response;
      if (searchKeyword) {
        response = await employeeProductAPI.search(searchKeyword, page, 10);
      } else if (selectedCategory) {
        response = await employeeProductAPI.getByCategory(selectedCategory, page, 10);
      } else {
        response = await employeeProductAPI.getAll(page, 10);
      }
      
      const data = response.data?.content || [];
      console.log('Loaded products:', data);
      console.log('Sample product thumbnailUrl:', data[0]?.thumbnailUrl);
      setProducts(data);
      
      if (response.data?.page?.totalPages !== undefined) {
        setTotalPages(response.data.page.totalPages);
      } else if (response.data?.totalPages !== undefined) {
        setTotalPages(response.data.totalPages);
      } else {
        setTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading products:', error);
      if (error.response?.status === 401) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
      } else if (error.response?.status === 403) {
        alert('Bạn không có quyền truy cập trang này.');
      } else {
        alert('Có lỗi xảy ra khi tải danh sách sản phẩm.');
      }
      setProducts([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const response = await categoryAPI.getAll(0, 100);
      const data = response.data?.content || response.data || [];
      setCategories(data);
    } catch (error) {
      console.error('Error loading categories:', error);
    }
  };

  const loadCategoriesForManagement = async () => {
    try {
      setCategoryLoading(true);
      const response = await categoryAPI.getAll(categoryPage, 10);
      const data = response.data?.content || [];
      setCategories(data);
      
      if (response.data?.page?.totalPages !== undefined) {
        setCategoryTotalPages(response.data.page.totalPages);
      } else if (response.data?.totalPages !== undefined) {
        setCategoryTotalPages(response.data.totalPages);
      } else {
        setCategoryTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading categories:', error);
      setCategories([]);
    } finally {
      setCategoryLoading(false);
    }
  };

  const loadAppointments = async () => {
    try {
      setAppointmentLoading(true);
      const response = await employeeAppointmentAPI.getAll(appointmentPage, 10);
      const data = response.data?.content || [];
      setAppointments(data);
      
      if (response.data?.page?.totalPages !== undefined) {
        setAppointmentTotalPages(response.data.page.totalPages || 1);
      } else {
        setAppointmentTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading appointments:', error);
      setAppointments([]);
    } finally {
      setAppointmentLoading(false);
    }
  };

  const loadDeliveryOrders = async () => {
    try {
      setDeliveryLoading(true);
      const response = await employeeDeliveryAPI.getDeliveryOrders(deliveryPage, 10);
      console.log('Delivery orders response:', response.data);
      
      if (response.data?.content) {
        setDeliveryOrders(response.data.content);
        
        // Update pagination info
        if (response.data.page) {
          setDeliveryTotalPages(response.data.page.totalPages || 1);
        }
      }
    } catch (error) {
      console.error('Error loading delivery orders:', error);
      if (error.response?.status === 404) {
        setDeliveryOrders([]);
      }
    } finally {
      setDeliveryLoading(false);
    }
  };

  const handleUpdateToDelivering = async (orderNumber) => {
    if (!window.confirm(`Bạn có chắc chắn muốn cập nhật đơn hàng ${orderNumber} thành "Đang giao hàng"?`)) {
      return;
    }

    try {
      console.log('Updating order to DELIVERING:', orderNumber);
      await employeeDeliveryAPI.updateToDelivering(orderNumber);
      alert('✅ Cập nhật thành "Đang giao hàng" thành công!');
      await loadDeliveryOrders();
    } catch (error) {
      console.error('Error updating order to delivering:', error);
      if (error.response?.data?.message) {
        alert(`❌ Lỗi: ${error.response.data.message}`);
      } else {
        alert('❌ Có lỗi xảy ra khi cập nhật trạng thái');
      }
    }
  };

  const handleUpdateToDelivered = async (orderNumber) => {
    if (!window.confirm(`Bạn có chắc chắn muốn cập nhật đơn hàng ${orderNumber} thành "Đã giao hàng"?`)) {
      return;
    }

    try {
      console.log('Updating order to DELIVERED:', orderNumber);
      await employeeDeliveryAPI.updateToDelivered(orderNumber);
      alert('✅ Cập nhật thành "Đã giao hàng" thành công!');
      await loadDeliveryOrders();
    } catch (error) {
      console.error('Error updating order to delivered:', error);
      if (error.response?.data?.message) {
        alert(`❌ Lỗi: ${error.response.data.message}`);
      } else {
        alert('❌ Có lỗi xảy ra khi cập nhật trạng thái');
      }
    }
  };

  const handleOpenDeliveryModal = (order) => {
    setSelectedOrderForDelivery(order);
    setDeliveryFormData({
      senderName: '',
      senderPhone: '',
      height: '',
      weight: '',
      width: '',
      length: '',
      requiredNote: 'KHONGCHOXEMHANG',
    });
    setShowDeliveryModal(true);
  };

  const handleCloseDeliveryModal = () => {
    setShowDeliveryModal(false);
    setSelectedOrderForDelivery(null);
    setDeliveryFormData({
      senderName: '',
      senderPhone: '',
      height: '',
      weight: '',
      width: '',
      length: '',
      requiredNote: 'KHONGCHOXEMHANG',
    });
  };

  const handleDeliveryFormChange = (e) => {
    const { name, value } = e.target;
    setDeliveryFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmitDelivery = async (e) => {
    e.preventDefault();
    
    if (!selectedOrderForDelivery) {
      alert('❌ Chưa chọn đơn hàng');
      return;
    }

    if (!deliveryFormData.senderName.trim()) {
      alert('❌ Vui lòng nhập tên người gửi');
      return;
    }

    if (!deliveryFormData.senderPhone.trim()) {
      alert('❌ Vui lòng nhập số điện thoại người gửi');
      return;
    }

    if (!deliveryFormData.height || !deliveryFormData.weight || !deliveryFormData.width || !deliveryFormData.length) {
      alert('❌ Vui lòng nhập đầy đủ kích thước bưu kiện (cao, nặng, rộng, dài)');
      return;
    }

    try {
      setDeliveryFormLoading(true);
      const payload = {
        orderNumber: selectedOrderForDelivery.orderNumber,
        senderName: deliveryFormData.senderName,
        senderPhone: deliveryFormData.senderPhone,
        senderAddress: SENDER_ADDRESS,
        height: parseInt(deliveryFormData.height),
        weight: parseInt(deliveryFormData.weight),
        width: parseInt(deliveryFormData.width),
        length: parseInt(deliveryFormData.length),
        requiredNote: deliveryFormData.requiredNote,
      };

      console.log('Submitting delivery:', payload);
      await employeeDeliveryAPI.createDelivery(payload);
      alert('✅ Đơn hàng đã được gửi giao hàng thành công!');
      handleCloseDeliveryModal();
      await loadDeliveryOrders();
    } catch (error) {
      console.error('Error submitting delivery:', error);
      if (error.response?.data?.message) {
        alert(`❌ Lỗi: ${error.response.data.message}`);
      } else {
        alert('❌ Có lỗi xảy ra khi gửi đơn hàng');
      }
    } finally {
      setDeliveryFormLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    loadProducts();
  };

  const resetForm = () => {
    setFormData({
      name: '',
      price: '',
      stockQuantity: '',
      status: 'ACTIVE',
      petType: 'CAT',
      categoryName: '',
    });
    setThumbnailFile(null);
    setImageFiles([]);
  };

  const handleCreateProduct = async (e) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      alert('Vui lòng nhập tên sản phẩm');
      return;
    }
    if (!formData.price || parseFloat(formData.price) <= 0) {
      alert('Vui lòng nhập giá hợp lệ');
      return;
    }
    if (!formData.stockQuantity || parseInt(formData.stockQuantity) < 0) {
      alert('Vui lòng nhập số lượng tồn kho hợp lệ');
      return;
    }
    if (!formData.categoryName) {
      alert('Vui lòng chọn danh mục');
      return;
    }
    if (!thumbnailFile) {
      alert('Vui lòng chọn ảnh thumbnail');
      return;
    }

    try {
      setFormLoading(true);
      const formDataToSend = new FormData();
      
      // Add product data as JSON string
      const productData = {
        name: formData.name.trim(),
        price: parseFloat(formData.price),
        stockQuantity: parseInt(formData.stockQuantity),
        status: formData.status,
        petType: formData.petType,
        categoryName: formData.categoryName,
      };
      formDataToSend.append('data', JSON.stringify(productData));
      
      // Add thumbnail
      formDataToSend.append('thumbnail', thumbnailFile);
      
      await employeeProductAPI.create(formDataToSend);
      alert('Đã tạo sản phẩm thành công!');
      resetForm();
      setShowCreateModal(false);
      loadProducts();
    } catch (error) {
      console.error('Error creating product:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo sản phẩm');
      }
    } finally {
      setFormLoading(false);
    }
  };

  const handleEditProduct = (product) => {
    setEditingProduct(product);
    setFormData({
      name: product.name || '',
      price: product.price?.toString() || '',
      stockQuantity: product.stockQuantity?.toString() || '',
      status: product.status || 'ACTIVE',
      petType: product.petType || 'CAT',
      categoryName: product.categoryName || '',
    });
    setThumbnailFile(null);
    setImageFiles([]);
    setShowEditModal(true);
  };

  const handleUpdateProduct = async (e) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      alert('Vui lòng nhập tên sản phẩm');
      return;
    }
    if (!formData.price || parseFloat(formData.price) <= 0) {
      alert('Vui lòng nhập giá hợp lệ');
      return;
    }
    if (!formData.stockQuantity || parseInt(formData.stockQuantity) < 0) {
      alert('Vui lòng nhập số lượng tồn kho hợp lệ');
      return;
    }
    if (!formData.categoryName) {
      alert('Vui lòng chọn danh mục');
      return;
    }

    if (!editingProduct) return;

    try {
      setFormLoading(true);
      const formDataToSend = new FormData();
      
      // Add product data as JSON string
      const productData = {
        name: formData.name.trim(),
        price: parseFloat(formData.price),
        stockQuantity: parseInt(formData.stockQuantity),
        status: formData.status,
        petType: formData.petType,
        categoryName: formData.categoryName,
      };
      formDataToSend.append('data', JSON.stringify(productData));
      
      // Add thumbnail if provided
      if (thumbnailFile) {
        formDataToSend.append('thumbnail', thumbnailFile);
      }
      
      await employeeProductAPI.update(editingProduct.id, formDataToSend);
      alert('Đã cập nhật sản phẩm thành công!');
      resetForm();
      setShowEditModal(false);
      setEditingProduct(null);
      loadProducts();
    } catch (error) {
      console.error('Error updating product:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi cập nhật sản phẩm');
      }
    } finally {
      setFormLoading(false);
    }
  };

  const handleDeleteProduct = async (productId) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
      return;
    }

    try {
      await employeeProductAPI.delete(productId);
      alert('Đã xóa sản phẩm thành công!');
      loadProducts();
    } catch (error) {
      console.error('Error deleting product:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi xóa sản phẩm');
      }
    }
  };

  const handleThumbnailChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setThumbnailFile(file);
    }
  };

  const handleImageFilesChange = (e) => {
    const files = Array.from(e.target.files);
    setImageFiles(files);
  };

  // Medical Record functions
  const handleSearchMedicalRecords = async (e) => {
    e.preventDefault();
    if (!searchPhoneNumber.trim()) {
      alert('Vui lòng nhập số điện thoại');
      return;
    }

    try {
      setMedicalRecordLoading(true);
      const response = await medicalRecordAPI.getByPhoneNumber(searchPhoneNumber.trim());
      // Handle response structure: { content: [...], page: {...} }
      const records = response.data?.content || [];
      setMedicalRecords(records);
    } catch (error) {
      console.error('Error searching medical records:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tìm kiếm bệnh án');
      }
      setMedicalRecords([]);
    } finally {
      setMedicalRecordLoading(false);
    }
  };

  const handleCreateMedicalRecord = async (e) => {
    e.preventDefault();
    
    if (!medicalRecordFormData.employeePhoneNumber.trim()) {
      alert('Vui lòng nhập số điện thoại nhân viên');
      return;
    }
    if (!medicalRecordFormData.pet.name.trim()) {
      alert('Vui lòng nhập tên thú cưng');
      return;
    }
    if (!medicalRecordFormData.owner.fullName.trim()) {
      alert('Vui lòng nhập tên chủ sở hữu');
      return;
    }
    if (!medicalRecordFormData.owner.phoneNumber.trim()) {
      alert('Vui lòng nhập số điện thoại chủ sở hữu');
      return;
    }

    try {
      setFormLoading(true);
      const recordData = {
        employeePhoneNumber: medicalRecordFormData.employeePhoneNumber.trim(),
        pet: {
          name: medicalRecordFormData.pet.name.trim(),
          type: medicalRecordFormData.pet.type,
          breed: medicalRecordFormData.pet.breed.trim() || '',
          age: medicalRecordFormData.pet.age ? parseInt(medicalRecordFormData.pet.age) : null,
        },
        owner: {
          fullName: medicalRecordFormData.owner.fullName.trim(),
          phoneNumber: medicalRecordFormData.owner.phoneNumber.trim(),
          address: medicalRecordFormData.owner.address.trim() || '',
        },
        recordDetails: {
          healthCondition: medicalRecordFormData.recordDetails.healthCondition.trim() || '',
          medicalHistory: medicalRecordFormData.recordDetails.medicalHistory.trim() || '',
          updatedDate: medicalRecordFormData.recordDetails.updatedDate || new Date().toISOString(),
          temperature: medicalRecordFormData.recordDetails.temperature.trim() || '',
          vaccines: medicalRecordFormData.recordDetails.vaccines.trim() || '',
          diagnosisResult: medicalRecordFormData.recordDetails.diagnosisResult.trim() || '',
        },
      };
      
      await medicalRecordAPI.create(recordData);
      alert('Đã tạo bệnh án thành công!');
      setShowCreateMedicalRecordModal(false);
      resetMedicalRecordForm();
    } catch (error) {
      console.error('Error creating medical record:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo bệnh án');
      }
    } finally {
      setFormLoading(false);
    }
  };

  const handleViewMedicalRecordDetails = async (recordId) => {
    try {
      setMedicalRecordLoading(true);
      const response = await medicalRecordAPI.getDetails(recordId, 0);
      // Handle response structure: { content: [...] } or direct array
      const details = response.data?.content || response.data || [];
      setMedicalRecordDetails(Array.isArray(details) ? details : [details]);
      setSelectedMedicalRecord(recordId);
      setShowMedicalRecordDetailsModal(true);
    } catch (error) {
      console.error('Error loading medical record details:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tải chi tiết bệnh án');
      }
    } finally {
      setMedicalRecordLoading(false);
    }
  };

  const handleEditDetail = (detail) => {
    setEditingDetail(detail);
    setDetailFormData({
      healthCondition: detail.healthCondition || '',
      medicalHistory: detail.medicalHistory || '',
      updatedDate: detail.updatedDate ? new Date(detail.updatedDate).toISOString().slice(0, 16) : '',
      temperature: detail.temperature?.toString() || '',
      vaccines: detail.vaccines || '',
      diagnosisResult: detail.diagnosisResult || '',
    });
    setShowUpdateDetailModal(true);
  };

  const handleUpdateDetail = async (e) => {
    e.preventDefault();
    
    if (!selectedMedicalRecord || !editingDetail) return;

    try {
      setFormLoading(true);
      const detailData = {
        healthCondition: detailFormData.healthCondition.trim() || '',
        medicalHistory: detailFormData.medicalHistory.trim() || '',
        updatedDate: detailFormData.updatedDate ? `${detailFormData.updatedDate}:00` : new Date().toISOString(),
        temperature: detailFormData.temperature ? parseFloat(detailFormData.temperature) : null,
        vaccines: detailFormData.vaccines.trim() || '',
        diagnosisResult: detailFormData.diagnosisResult.trim() || '',
      };
      
      await medicalRecordAPI.updateDetail(selectedMedicalRecord, detailData);
      alert('Đã cập nhật chi tiết bệnh án thành công!');
      setShowUpdateDetailModal(false);
      setEditingDetail(null);
      // Reload details
      handleViewMedicalRecordDetails(selectedMedicalRecord);
    } catch (error) {
      console.error('Error updating detail:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi cập nhật chi tiết bệnh án');
      }
    } finally {
      setFormLoading(false);
    }
  };

  const resetMedicalRecordForm = () => {
    setMedicalRecordFormData({
      employeePhoneNumber: '',
      pet: {
        name: '',
        type: 'dog',
        breed: '',
        age: '',
      },
      owner: {
        fullName: '',
        phoneNumber: '',
        address: '',
      },
      recordDetails: {
        healthCondition: '',
        medicalHistory: '',
        updatedDate: '',
        temperature: '',
        vaccines: '',
        diagnosisResult: '',
      },
    });
  };

  // Appointment functions
  const handleCreateAppointment = async (e) => {
    e.preventDefault();
    
    if (!appointmentFormData.customerPhoneNumber.trim()) {
      alert('Vui lòng nhập số điện thoại khách hàng');
      return;
    }
    if (!appointmentFormData.appointmentTime) {
      alert('Vui lòng chọn thời gian hẹn');
      return;
    }
    if (!user?.userId) {
      alert('Không tìm thấy thông tin nhân viên');
      return;
    }

    try {
      setAppointmentFormLoading(true);
      const formattedDateTime = appointmentFormData.appointmentTime 
        ? `${appointmentFormData.appointmentTime}:00`
        : '';
      
      const appointmentData = {
        customerPhoneNumber: appointmentFormData.customerPhoneNumber.trim(),
        employeeId: user.userId,
        appointmentTime: formattedDateTime,
        status: appointmentFormData.status,
        description: appointmentFormData.description.trim() || '',
      };
      
      await employeeAppointmentAPI.create(appointmentData);
      alert('Đã tạo lịch hẹn thành công!');
      setShowCreateAppointmentModal(false);
      resetAppointmentForm();
      loadAppointments();
    } catch (error) {
      console.error('Error creating appointment:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo lịch hẹn');
      }
    } finally {
      setAppointmentFormLoading(false);
    }
  };

  const handleUpdateAppointment = async (e) => {
    e.preventDefault();
    
    if (!editingAppointment) return;
    if (!appointmentFormData.appointmentTime) {
      alert('Vui lòng chọn thời gian hẹn');
      return;
    }

    // Get userId from localStorage or user context
    const userId = user?.userId || localStorage.getItem('userId');
    if (!userId) {
      alert('Không tìm thấy thông tin nhân viên');
      return;
    }

    try {
      setAppointmentFormLoading(true);
      // Format datetime: convert from "YYYY-MM-DDTHH:mm" to "YYYY-MM-DDTHH:mm:00"
      const formattedDateTime = appointmentFormData.appointmentTime 
        ? `${appointmentFormData.appointmentTime}:00`
        : '';
      
      const appointmentData = {
        employeeId: userId,
        appointmentTime: formattedDateTime,
        status: appointmentFormData.status,
        description: appointmentFormData.description.trim() || '',
      };
      
      await employeeAppointmentAPI.update(editingAppointment.id, appointmentData);
      alert('Đã cập nhật lịch hẹn thành công!');
      setShowUpdateAppointmentModal(false);
      setEditingAppointment(null);
      resetAppointmentForm();
      loadAppointments();
    } catch (error) {
      console.error('Error updating appointment:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi cập nhật lịch hẹn');
      }
    } finally {
      setAppointmentFormLoading(false);
    }
  };

  const handleEditAppointment = (appointment) => {
    setEditingAppointment(appointment);
    setAppointmentFormData({
      customerPhoneNumber: appointment.customer?.phoneNumber || '',
      employeeId: user?.userId || '',
      appointmentTime: appointment.appointmentTime ? new Date(appointment.appointmentTime).toISOString().slice(0, 16) : '',
      status: appointment.status || 'SCHEDULED',
      description: appointment.description || '',
    });
    setShowUpdateAppointmentModal(true);
  };

  const resetAppointmentForm = () => {
    setAppointmentFormData({
      customerPhoneNumber: '',
      employeeId: user?.userId || '',
      appointmentTime: '',
      status: 'SCHEDULED',
      description: '',
    });
  };

  // Category management functions
  const handleCreateCategory = async (e) => {
    e.preventDefault();
    
    if (!categoryFormData.name.trim()) {
      alert('Vui lòng nhập tên danh mục');
      return;
    }

    try {
      setCategoryFormLoading(true);
      await categoryAPI.create({
        name: categoryFormData.name.trim(),
        description: categoryFormData.description.trim() || '',
      });
      alert('Đã tạo danh mục thành công!');
      setShowCreateCategoryModal(false);
      resetCategoryForm();
      loadCategoriesForManagement();
      loadCategories(); // Also reload for product form dropdown
    } catch (error) {
      console.error('Error creating category:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo danh mục');
      }
    } finally {
      setCategoryFormLoading(false);
    }
  };

  const handleEditCategory = (category) => {
    setEditingCategory(category);
    setCategoryFormData({
      name: category.name || '',
      description: category.description || '',
    });
  };

  const handleCancelEdit = () => {
    setEditingCategory(null);
    resetCategoryForm();
  };

  const handleUpdateCategory = async (e) => {
    e.preventDefault();
    
    if (!categoryFormData.name.trim()) {
      alert('Vui lòng nhập tên danh mục');
      return;
    }

    if (!editingCategory) {
      return;
    }

    try {
      setCategoryFormLoading(true);
      await categoryAPI.update(editingCategory.id, {
        name: categoryFormData.name.trim(),
        description: categoryFormData.description.trim() || '',
      });
      alert('Đã cập nhật danh mục thành công!');
      setEditingCategory(null);
      resetCategoryForm();
      loadCategoriesForManagement();
      loadCategories(); // Also reload for product form dropdown
    } catch (error) {
      console.error('Error updating category:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi cập nhật danh mục');
      }
    } finally {
      setCategoryFormLoading(false);
    }
  };

  const resetCategoryForm = () => {
    setCategoryFormData({
      name: '',
      description: '',
    });
  };

  // Discount functions
  const loadDiscounts = async () => {
    try {
      setDiscountLoading(true);
      const response = await employeeDiscountAPI.getByStatus(discountStatus, discountPage, 10);
      const data = response.data?.content || [];
      setDiscounts(data);
      
      if (response.data?.page?.totalPages !== undefined) {
        setDiscountTotalPages(response.data.page.totalPages || 1);
      } else {
        setDiscountTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading discounts:', error);
      setDiscounts([]);
    } finally {
      setDiscountLoading(false);
    }
  };

  useEffect(() => {
    if (activeTab === 'discounts') {
      loadDiscounts();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTab, discountPage, discountStatus]);

  const handleCreateManualDiscount = async (e) => {
    e.preventDefault();
    
    if (!manualDiscountFormData.discountCode.trim()) {
      alert('Vui lòng nhập mã giảm giá');
      return;
    }
    if (!manualDiscountFormData.discountName.trim()) {
      alert('Vui lòng nhập tên mã giảm giá');
      return;
    }
    if (!manualDiscountFormData.percent || parseFloat(manualDiscountFormData.percent) <= 0) {
      alert('Vui lòng nhập phần trăm giảm giá hợp lệ');
      return;
    }
    if (!manualDiscountFormData.startDate) {
      alert('Vui lòng chọn ngày bắt đầu');
      return;
    }
    if (!manualDiscountFormData.endDate) {
      alert('Vui lòng chọn ngày kết thúc');
      return;
    }

    try {
      setDiscountFormLoading(true);
      const discountData = {
        discountCode: manualDiscountFormData.discountCode.trim(),
        discountName: manualDiscountFormData.discountName.trim(),
        percent: parseFloat(manualDiscountFormData.percent),
        startDate: `${manualDiscountFormData.startDate}:00`,
        endDate: `${manualDiscountFormData.endDate}:00`,
      };
      
      await employeeDiscountAPI.createManually(discountData);
      alert('Đã tạo mã giảm giá thành công!');
      setShowCreateManualDiscountModal(false);
      resetManualDiscountForm();
      loadDiscounts();
    } catch (error) {
      console.error('Error creating manual discount:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo mã giảm giá');
      }
    } finally {
      setDiscountFormLoading(false);
    }
  };

  const handleCreateAutoDiscount = async (e) => {
    e.preventDefault();
    
    if (!autoDiscountFormData.discountName.trim()) {
      alert('Vui lòng nhập tên mã giảm giá');
      return;
    }
    if (!autoDiscountFormData.percent || parseFloat(autoDiscountFormData.percent) <= 0) {
      alert('Vui lòng nhập phần trăm giảm giá hợp lệ');
      return;
    }
    if (!autoDiscountFormData.startDate) {
      alert('Vui lòng chọn ngày bắt đầu');
      return;
    }
    if (!autoDiscountFormData.endDate) {
      alert('Vui lòng chọn ngày kết thúc');
      return;
    }
    if (autoDiscountFormData.totalLimit && parseInt(autoDiscountFormData.totalLimit) <= 0) {
      alert('Vui lòng nhập giới hạn số lượng hợp lệ');
      return;
    }

    try {
      setDiscountFormLoading(true);
      const discountData = {
        discountName: autoDiscountFormData.discountName.trim(),
        percent: parseFloat(autoDiscountFormData.percent),
        startDate: `${autoDiscountFormData.startDate}:00`,
        endDate: `${autoDiscountFormData.endDate}:00`,
        totalLimit: autoDiscountFormData.totalLimit ? parseInt(autoDiscountFormData.totalLimit) : null,
      };
      
      await employeeDiscountAPI.createAutomatically(discountData);
      alert('Đã tạo mã giảm giá tự động thành công!');
      setShowCreateAutoDiscountModal(false);
      resetAutoDiscountForm();
      loadDiscounts();
    } catch (error) {
      console.error('Error creating auto discount:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo mã giảm giá tự động');
      }
    } finally {
      setDiscountFormLoading(false);
    }
  };

  const resetManualDiscountForm = () => {
    setManualDiscountFormData({
      discountCode: '',
      discountName: '',
      percent: '',
      startDate: '',
      endDate: '',
    });
  };

  const resetAutoDiscountForm = () => {
    setAutoDiscountFormData({
      discountName: '',
      percent: '',
      startDate: '',
      endDate: '',
      totalLimit: '',
    });
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Quản Lý</h1>
        <div className="flex gap-2">
          {activeTab === 'products' && (
            <Button onClick={() => setShowCreateModal(true)}>
              Tạo Sản Phẩm Mới
            </Button>
          )}
          {activeTab === 'medicalRecords' && (
            <Button onClick={() => setShowCreateMedicalRecordModal(true)}>
              Tạo Bệnh Án Mới
            </Button>
          )}
          {activeTab === 'appointments' && (
            <Button onClick={() => setShowCreateAppointmentModal(true)}>
              Tạo Lịch Hẹn Mới
            </Button>
          )}
          {activeTab === 'categories' && (
            <Button onClick={() => setShowCreateCategoryModal(true)}>
              Tạo Danh Mục Mới
            </Button>
          )}
          {activeTab === 'discounts' && (
            <div className="flex gap-2">
              <Button onClick={() => setShowCreateManualDiscountModal(true)}>
                Tạo Mã Thủ Công
              </Button>
              <Button onClick={() => setShowCreateAutoDiscountModal(true)}>
                Tạo Mã Tự Động
              </Button>
            </div>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-2 mb-6 border-b">
        <button
          onClick={() => setActiveTab('products')}
          className={`px-6 py-3 font-medium transition ${
            activeTab === 'products'
              ? 'border-b-2 border-primary-600 text-primary-600'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Quản Lý Sản Phẩm
        </button>
        <button
          onClick={() => setActiveTab('medicalRecords')}
          className={`px-6 py-3 font-medium transition ${
            activeTab === 'medicalRecords'
              ? 'border-b-2 border-primary-600 text-primary-600'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Quản Lý Bệnh Án
        </button>
        <button
          onClick={() => {
            setActiveTab('delivery');
            loadDeliveryOrders();
          }}
          className={`px-6 py-3 font-medium transition ${
            activeTab === 'delivery'
              ? 'border-b-2 border-primary-600 text-primary-600'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Quản Lý Giao Hàng
        </button>
        <button
          onClick={() => {
            setActiveTab('appointments');
            loadAppointments();
          }}
          className={`px-6 py-3 font-medium transition ${
            activeTab === 'appointments'
              ? 'border-b-2 border-primary-600 text-primary-600'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Quản Lý Lịch Hẹn
        </button>
        <button
          onClick={() => {
            setActiveTab('categories');
            loadCategoriesForManagement();
          }}
          className={`px-6 py-3 font-medium transition ${
            activeTab === 'categories'
              ? 'border-b-2 border-primary-600 text-primary-600'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Quản Lý Danh Mục
        </button>
        <button
          onClick={() => {
            setActiveTab('discounts');
            loadDiscounts();
          }}
          className={`px-6 py-3 font-medium transition ${
            activeTab === 'discounts'
              ? 'border-b-2 border-primary-600 text-primary-600'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Quản Lý Mã Giảm Giá
        </button>
      </div>

      {/* Products Tab */}
      {activeTab === 'products' && (
        <>
          {/* Search and Filter */}
          <Card className="mb-8 bg-gradient-to-r from-purple-50 to-pink-50 border-purple-200">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-3 bg-purple-100 rounded-lg">
                <svg className="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <div>
                <h2 className="text-2xl font-bold text-gray-800">Tìm Kiếm & Lọc Sản Phẩm</h2>
                <p className="text-sm text-gray-600">Tìm kiếm theo tên hoặc lọc theo danh mục</p>
              </div>
            </div>
            <form onSubmit={handleSearch} className="flex gap-4 mb-4">
              <div className="flex-1 relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
                <Input
                  placeholder="Nhập tên sản phẩm để tìm kiếm..."
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Button type="submit" disabled={loading} className="min-w-[120px]">
                {loading ? (
                  <span className="flex items-center gap-2">
                    <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Đang tìm...
                  </span>
                ) : (
                  'Tìm Kiếm'
                )}
              </Button>
            </form>

            <div className="mt-4">
              <p className="text-sm font-medium text-gray-700 mb-2">Lọc theo danh mục:</p>
              <div className="flex gap-2 flex-wrap">
                <button
                  type="button"
                  onClick={() => {
                    setSelectedCategory('');
                    setPage(0);
                  }}
                  className={`px-4 py-2 rounded-lg font-medium transition ${
                    selectedCategory === ''
                      ? 'bg-purple-600 text-white shadow-md'
                      : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-300'
                  }`}
                >
                  <span className="flex items-center gap-2">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                    </svg>
                    Tất Cả
                  </span>
                </button>
                {categories.map((category) => (
                  <button
                    key={category.id || category.name}
                    type="button"
                    onClick={() => {
                      setSelectedCategory(category.name);
                      setPage(0);
                    }}
                    className={`px-4 py-2 rounded-lg font-medium transition ${
                      selectedCategory === category.name
                        ? 'bg-purple-600 text-white shadow-md'
                        : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-300'
                    }`}
                  >
                    {category.name}
                  </button>
                ))}
              </div>
            </div>
          </Card>

          {/* Products Grid */}
          {loading ? (
            <Card className="mb-8">
              <div className="text-center py-12">
                <svg className="animate-spin h-8 w-8 text-primary-600 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p className="text-gray-500">Đang tải sản phẩm...</p>
              </div>
            </Card>
          ) : products.length === 0 ? (
            <Card className="mb-8">
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Không tìm thấy sản phẩm nào</p>
                <p className="text-gray-400 text-sm">Thử thay đổi từ khóa tìm kiếm hoặc danh mục</p>
              </div>
            </Card>
          ) : (
            <>
              <Card className="mb-8">
                <div className="flex items-center justify-between mb-6">
                  <div>
                    <h2 className="text-2xl font-bold text-gray-800">Danh Sách Sản Phẩm</h2>
                    <p className="text-sm text-gray-600 mt-1">Tìm thấy {products.length} sản phẩm</p>
                  </div>
                  <div className="flex items-center gap-2 px-4 py-2 bg-green-50 rounded-lg">
                    <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span className="text-green-700 font-medium">{products.length} sản phẩm</span>
                  </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                  {products.map((product) => (
                    <Card
                      key={product.id}
                      className="p-5 hover:shadow-lg transition-all duration-300 border-l-4 border-purple-500 group"
                    >
                      {/* Product Image */}
                      <div className="mb-4 relative overflow-hidden rounded-lg bg-gray-100 aspect-square flex items-center justify-center">
                        {product.thumbnailUrl && product.id ? (
                          <ProductImage 
                            thumbnailUrl={product.thumbnailUrl} 
                            productId={product.id}
                            productName={product.name}
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-gray-400">
                            <svg className="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                          </div>
                        )}
                        <div className="absolute top-2 right-2">
                          <span
                            className={`px-2 py-1 rounded-full text-xs font-semibold ${
                              product.status === 'ACTIVE'
                                ? 'bg-green-500 text-white'
                                : 'bg-gray-500 text-white'
                            }`}
                          >
                            {product.status === 'ACTIVE' ? 'Hoạt động' : 'Ngừng'}
                          </span>
                        </div>
                      </div>

                      {/* Product Info */}
                      <div className="space-y-3">
                        <div>
                          <h3 className="font-bold text-lg text-gray-800 line-clamp-2 group-hover:text-purple-600 transition">
                            {product.name || 'N/A'}
                          </h3>
                        </div>

                        <div className="flex items-center gap-2 text-sm text-gray-600">
                          <svg className="w-4 h-4 text-purple-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          <span className="font-bold text-lg text-purple-600">
                            {product.price?.toLocaleString('vi-VN')} VNĐ
                          </span>
                        </div>

                        <div className="grid grid-cols-2 gap-2 text-sm">
                          <div className="flex items-center gap-1">
                            <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                            </svg>
                            <span className="text-gray-600">{product.categoryName || 'N/A'}</span>
                          </div>
                          <div className="flex items-center gap-1">
                            <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                            <span className="text-gray-600">{product.petType || 'N/A'}</span>
                          </div>
                        </div>

                        <div className="flex items-center gap-2 pt-2 border-t border-gray-200">
                          <div className="flex items-center gap-1 text-sm">
                            <svg className="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                            </svg>
                            <span className={`font-semibold ${
                              (product.stockQuantity || 0) > 10 
                                ? 'text-green-600' 
                                : (product.stockQuantity || 0) > 0 
                                  ? 'text-orange-600' 
                                  : 'text-red-600'
                            }`}>
                              {product.stockQuantity || 0} sản phẩm
                            </span>
                          </div>
                        </div>

                        {/* Actions */}
                        <div className="flex gap-2 pt-2">
                          <Button
                            variant="secondary"
                            className="flex-1 text-sm"
                            onClick={() => handleEditProduct(product)}
                          >
                            <span className="flex items-center justify-center gap-1">
                              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                              </svg>
                              Sửa
                            </span>
                          </Button>
                          <Button
                            variant="danger"
                            className="flex-1 text-sm"
                            onClick={() => handleDeleteProduct(product.id)}
                          >
                            <span className="flex items-center justify-center gap-1">
                              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                              </svg>
                              Xóa
                            </span>
                          </Button>
                        </div>
                      </div>
                    </Card>
                  ))}
                </div>
              </Card>

              {/* Pagination */}
              {totalPages > 1 && (
                <Card className="mt-6">
                  <div className="flex justify-center items-center gap-4">
                    <Button
                      variant="secondary"
                      onClick={() => setPage((p) => Math.max(0, p - 1))}
                      disabled={page === 0}
                      className="flex items-center gap-2"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                      </svg>
                      Trước
                    </Button>
                    <div className="flex items-center gap-2">
                      <span className="px-4 py-2 bg-purple-50 text-purple-700 rounded-lg font-semibold">
                        Trang {page + 1} / {totalPages}
                      </span>
                    </div>
                    <Button
                      variant="secondary"
                      onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                      disabled={page >= totalPages - 1}
                      className="flex items-center gap-2"
                    >
                      Sau
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                      </svg>
                    </Button>
                  </div>
                </Card>
              )}
            </>
          )}
        </>
      )}

      {/* Create Product Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => {
          setShowCreateModal(false);
          resetForm();
        }}
        title={
          <div className="flex items-center gap-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
            </div>
            <span>Tạo Sản Phẩm Mới</span>
          </div>
        }
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowCreateModal(false);
                resetForm();
              }}
            >
              Hủy
            </Button>
            <Button onClick={handleCreateProduct} disabled={formLoading}>
              {formLoading ? 'Đang tạo...' : 'Tạo Sản Phẩm'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleCreateProduct} className="max-h-[70vh] overflow-y-auto pr-2">
          <div className="mb-6 p-4 bg-blue-50 rounded-lg border border-blue-200">
            <div className="flex items-center gap-2 mb-4">
              <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h3 className="text-lg font-bold text-blue-800">Thông Tin Cơ Bản</h3>
            </div>
            <Input
              label="Tên Sản Phẩm"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
              placeholder="Nhập tên sản phẩm..."
            />
            <div className="grid grid-cols-2 gap-4 mt-4">
              <Input
                label="Giá (VNĐ)"
                type="number"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                required
                min="0"
                step="1000"
                placeholder="0"
              />
              <Input
                label="Số Lượng Tồn Kho"
                type="number"
                value={formData.stockQuantity}
                onChange={(e) => setFormData({ ...formData, stockQuantity: e.target.value })}
                required
                min="0"
                placeholder="0"
              />
            </div>
          </div>

          <div className="mb-6 p-4 bg-purple-50 rounded-lg border border-purple-200">
            <div className="flex items-center gap-2 mb-4">
              <svg className="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
              <h3 className="text-lg font-bold text-purple-800">Phân Loại</h3>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Trạng Thái <span className="text-red-500">*</span>
                </label>
                <select
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  required
                >
                  <option value="ACTIVE">Hoạt động</option>
                  <option value="INACTIVE">Ngừng hoạt động</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Loại Thú Cưng <span className="text-red-500">*</span>
                </label>
                <select
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  value={formData.petType}
                  onChange={(e) => setFormData({ ...formData, petType: e.target.value })}
                  required
                >
                  <option value="CAT">Mèo</option>
                  <option value="DOG">Chó</option>
                  <option value="BIRD">Chim</option>
                  <option value="OTHER">Khác</option>
                </select>
              </div>
            </div>
            <div className="mt-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Danh Mục <span className="text-red-500">*</span>
              </label>
              <select
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                value={formData.categoryName}
                onChange={(e) => setFormData({ ...formData, categoryName: e.target.value })}
                required
              >
                <option value="">Chọn danh mục</option>
                {categories.map((category) => (
                  <option key={category.id || category.name} value={category.name}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="mb-6 p-4 bg-orange-50 rounded-lg border border-orange-200">
            <div className="flex items-center gap-2 mb-4">
              <svg className="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <h3 className="text-lg font-bold text-orange-800">Hình Ảnh</h3>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Ảnh Thumbnail <span className="text-red-500">*</span>
              </label>
              <input
                type="file"
                accept="image/*"
                onChange={handleThumbnailChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              />
              {thumbnailFile && (
                <div className="mt-2 flex items-center gap-2 text-sm text-green-600">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>Đã chọn: {thumbnailFile.name}</span>
                </div>
              )}
            </div>
          </div>
        </form>
      </Modal>

      {/* Edit Product Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => {
          setShowEditModal(false);
          setEditingProduct(null);
          resetForm();
        }}
        title={
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </div>
            <span>Chỉnh Sửa Sản Phẩm</span>
          </div>
        }
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowEditModal(false);
                setEditingProduct(null);
                resetForm();
              }}
            >
              Hủy
            </Button>
            <Button onClick={handleUpdateProduct} disabled={formLoading}>
              {formLoading ? 'Đang cập nhật...' : 'Cập Nhật'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleUpdateProduct} className="max-h-[70vh] overflow-y-auto pr-2">
          <div className="mb-6 p-4 bg-blue-50 rounded-lg border border-blue-200">
            <div className="flex items-center gap-2 mb-4">
              <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h3 className="text-lg font-bold text-blue-800">Thông Tin Cơ Bản</h3>
            </div>
            <Input
              label="Tên Sản Phẩm"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
              placeholder="Nhập tên sản phẩm..."
            />
            <div className="grid grid-cols-2 gap-4 mt-4">
              <Input
                label="Giá (VNĐ)"
                type="number"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                required
                min="0"
                step="1000"
                placeholder="0"
              />
              <Input
                label="Số Lượng Tồn Kho"
                type="number"
                value={formData.stockQuantity}
                onChange={(e) => setFormData({ ...formData, stockQuantity: e.target.value })}
                required
                min="0"
                placeholder="0"
              />
            </div>
          </div>

          <div className="mb-6 p-4 bg-purple-50 rounded-lg border border-purple-200">
            <div className="flex items-center gap-2 mb-4">
              <svg className="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
              <h3 className="text-lg font-bold text-purple-800">Phân Loại</h3>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Trạng Thái <span className="text-red-500">*</span>
                </label>
                <select
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  required
                >
                  <option value="ACTIVE">Hoạt động</option>
                  <option value="INACTIVE">Ngừng hoạt động</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Loại Thú Cưng <span className="text-red-500">*</span>
                </label>
                <select
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  value={formData.petType}
                  onChange={(e) => setFormData({ ...formData, petType: e.target.value })}
                  required
                >
                  <option value="CAT">Mèo</option>
                  <option value="DOG">Chó</option>
                  <option value="BIRD">Chim</option>
                  <option value="OTHER">Khác</option>
                </select>
              </div>
            </div>
            <div className="mt-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Danh Mục <span className="text-red-500">*</span>
              </label>
              <select
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                value={formData.categoryName}
                onChange={(e) => setFormData({ ...formData, categoryName: e.target.value })}
                required
              >
                <option value="">Chọn danh mục</option>
                {categories.map((category) => (
                  <option key={category.id || category.name} value={category.name}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="mb-6 p-4 bg-orange-50 rounded-lg border border-orange-200">
            <div className="flex items-center gap-2 mb-4">
              <svg className="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <h3 className="text-lg font-bold text-orange-800">Hình Ảnh</h3>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Ảnh Thumbnail (để trống nếu không thay đổi)
              </label>
              <input
                type="file"
                accept="image/*"
                onChange={handleThumbnailChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              {thumbnailFile && (
                <div className="mt-2 flex items-center gap-2 text-sm text-green-600">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>Đã chọn: {thumbnailFile.name}</span>
                </div>
              )}
              {editingProduct?.thumbnailUrl && !thumbnailFile && (
                <div className="mt-2 flex items-center gap-2 text-sm text-gray-500">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span>Đang sử dụng ảnh hiện tại</span>
                </div>
              )}
            </div>
          </div>
        </form>
      </Modal>

      {/* Medical Records Tab */}
      {activeTab === 'medicalRecords' && (
        <>
          {/* Search Medical Records */}
          <Card className="mb-8 bg-gradient-to-r from-blue-50 to-indigo-50 border-blue-200">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-3 bg-blue-100 rounded-lg">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <div>
                <h2 className="text-2xl font-bold text-gray-800">Tìm Kiếm Bệnh Án</h2>
                <p className="text-sm text-gray-600">Nhập số điện thoại chủ sở hữu để tìm kiếm</p>
              </div>
            </div>
            <form onSubmit={handleSearchMedicalRecords} className="flex gap-4">
              <div className="flex-1 relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                  </svg>
                </div>
                <Input
                  placeholder="Ví dụ: 0827563759"
                  value={searchPhoneNumber}
                  onChange={(e) => setSearchPhoneNumber(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Button type="submit" disabled={medicalRecordLoading} className="min-w-[120px]">
                {medicalRecordLoading ? (
                  <span className="flex items-center gap-2">
                    <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Đang tìm...
                  </span>
                ) : (
                  'Tìm Kiếm'
                )}
              </Button>
            </form>
          </Card>

          {/* Medical Records List */}
          {medicalRecordLoading && medicalRecords.length === 0 ? (
            <Card className="mb-8">
              <div className="text-center py-12">
                <svg className="animate-spin h-8 w-8 text-primary-600 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p className="text-gray-500">Đang tải dữ liệu...</p>
              </div>
            </Card>
          ) : medicalRecords.length > 0 ? (
            <Card className="mb-8">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h2 className="text-2xl font-bold text-gray-800">Danh Sách Bệnh Án</h2>
                  <p className="text-sm text-gray-600 mt-1">Tìm thấy {medicalRecords.length} bệnh án</p>
                </div>
                <div className="flex items-center gap-2 px-4 py-2 bg-green-50 rounded-lg">
                  <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span className="text-green-700 font-medium">{medicalRecords.length} kết quả</span>
                </div>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {medicalRecords.map((record) => (
                  <Card 
                    key={record.medicalRecordId} 
                    className="p-5 hover:shadow-lg transition-shadow cursor-pointer border-l-4 border-blue-500"
                    onClick={() => handleViewMedicalRecordDetails(record.medicalRecordId)}
                  >
                    <div className="flex items-start justify-between mb-3">
                      <div className="flex items-center gap-3">
                        <div className="p-2 bg-blue-100 rounded-lg">
                          {record.pet?.type === 'dog' ? (
                            <svg className="w-6 h-6 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                              <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                              <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
                            </svg>
                          ) : record.pet?.type === 'cat' ? (
                            <svg className="w-6 h-6 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M3 5a2 2 0 012-2h10a2 2 0 012 2v8a2 2 0 01-2 2h-2.22l.123.489.804.804A1 1 0 0113 18H7a1 1 0 01-.707-1.707l.804-.804L7.22 15H5a2 2 0 01-2-2V5zm5.771 7H5V5h10v7H8.771z" clipRule="evenodd" />
                            </svg>
                          ) : (
                            <svg className="w-6 h-6 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clipRule="evenodd" />
                            </svg>
                          )}
                        </div>
                        <div>
                          <h3 className="font-bold text-lg text-gray-800">{record.pet?.name || 'N/A'}</h3>
                          <p className="text-sm text-gray-500">{record.pet?.type || 'N/A'}</p>
                        </div>
                      </div>
                      <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        (record.medicalRecordDetails?.length || 0) > 0
                          ? 'bg-blue-100 text-blue-700'
                          : 'bg-gray-100 text-gray-600'
                      }`}>
                        {record.medicalRecordDetails?.length || 0} lần
                      </span>
                    </div>
                    
                    <div className="space-y-2 mt-4 pt-4 border-t border-gray-200">
                      <div className="flex items-center gap-2 text-sm">
                        <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                        </svg>
                        <span className="text-gray-600"><strong>Giống:</strong> {record.pet?.breed || 'N/A'}</span>
                      </div>
                      <div className="flex items-center gap-2 text-sm">
                        <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <span className="text-gray-600"><strong>Tuổi:</strong> {record.pet?.age ? `${record.pet.age} tuổi` : 'N/A'}</span>
                      </div>
                      <div className="flex items-center gap-2 text-sm">
                        <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                        <span className="text-gray-600"><strong>Người tạo:</strong> {record.createdBy || 'N/A'}</span>
                      </div>
                      <div className="flex items-center gap-2 text-sm">
                        <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span className="text-gray-600">
                          <strong>Ngày tạo:</strong> {
                            record.createdDate 
                              ? new Date(record.createdDate).toLocaleDateString('vi-VN', {
                                  year: 'numeric',
                                  month: 'short',
                                  day: 'numeric',
                                })
                              : 'N/A'
                          }
                        </span>
                      </div>
                    </div>

                    <div className="mt-4 pt-4 border-t border-gray-200">
                      <Button
                        variant="primary"
                        className="w-full"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleViewMedicalRecordDetails(record.medicalRecordId);
                        }}
                      >
                        <span className="flex items-center justify-center gap-2">
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                          </svg>
                          Xem Chi Tiết
                        </span>
                      </Button>
                    </div>
                  </Card>
                ))}
              </div>
            </Card>
          ) : searchPhoneNumber && !medicalRecordLoading ? (
            <Card className="mb-8">
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Không tìm thấy bệnh án nào</p>
                <p className="text-gray-400 text-sm">Vui lòng thử lại với số điện thoại khác</p>
              </div>
            </Card>
          ) : null}

          {/* Create Medical Record Modal */}
          <Modal
            isOpen={showCreateMedicalRecordModal}
            onClose={() => {
              setShowCreateMedicalRecordModal(false);
              resetMedicalRecordForm();
            }}
            title={
              <div className="flex items-center gap-3">
                <div className="p-2 bg-green-100 rounded-lg">
                  <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                  </svg>
                </div>
                <span>Tạo Bệnh Án Mới</span>
              </div>
            }
            footer={
              <>
                <Button
                  variant="secondary"
                  onClick={() => {
                    setShowCreateMedicalRecordModal(false);
                    resetMedicalRecordForm();
                  }}
                >
                  Hủy
                </Button>
                <Button onClick={handleCreateMedicalRecord} disabled={formLoading}>
                  {formLoading ? 'Đang tạo...' : 'Tạo Bệnh Án'}
                </Button>
              </>
            }
          >
            <form onSubmit={handleCreateMedicalRecord} className="max-h-[70vh] overflow-y-auto pr-2">
              <div className="mb-6 p-4 bg-blue-50 rounded-lg border border-blue-200">
                <div className="flex items-center gap-2 mb-2">
                  <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  <h3 className="text-lg font-bold text-blue-800">Thông Tin Nhân Viên</h3>
                </div>
                <Input
                  label="Số Điện Thoại Nhân Viên"
                  placeholder="05738572634"
                  value={medicalRecordFormData.employeePhoneNumber}
                  onChange={(e) => setMedicalRecordFormData({
                    ...medicalRecordFormData,
                    employeePhoneNumber: e.target.value,
                  })}
                  required
                />
              </div>
              
              <div className="mb-6 p-4 bg-purple-50 rounded-lg border border-purple-200">
                <div className="flex items-center gap-2 mb-4">
                  <svg className="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                  </svg>
                  <h3 className="text-lg font-bold text-purple-800">Thông Tin Thú Cưng</h3>
                </div>
              <Input
                label="Tên Thú Cưng"
                value={medicalRecordFormData.pet.name}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  pet: { ...medicalRecordFormData.pet, name: e.target.value },
                })}
                required
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Loại <span className="text-red-500">*</span>
                </label>
                <select
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  value={medicalRecordFormData.pet.type}
                  onChange={(e) => setMedicalRecordFormData({
                    ...medicalRecordFormData,
                    pet: { ...medicalRecordFormData.pet, type: e.target.value },
                  })}
                  required
                >
                  <option value="dog">Chó</option>
                  <option value="cat">Mèo</option>
                  <option value="bird">Chim</option>
                  <option value="other">Khác</option>
                </select>
              </div>
              <Input
                label="Giống"
                value={medicalRecordFormData.pet.breed}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  pet: { ...medicalRecordFormData.pet, breed: e.target.value },
                })}
              />
              <Input
                label="Tuổi"
                type="number"
                value={medicalRecordFormData.pet.age}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  pet: { ...medicalRecordFormData.pet, age: e.target.value },
                })}
              />
              </div>

              <div className="mb-6 p-4 bg-green-50 rounded-lg border border-green-200">
                <div className="flex items-center gap-2 mb-4">
                  <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  <h3 className="text-lg font-bold text-green-800">Thông Tin Chủ Sở Hữu</h3>
                </div>
              <Input
                label="Họ và Tên"
                value={medicalRecordFormData.owner.fullName}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  owner: { ...medicalRecordFormData.owner, fullName: e.target.value },
                })}
                required
              />
              <Input
                label="Số Điện Thoại"
                value={medicalRecordFormData.owner.phoneNumber}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  owner: { ...medicalRecordFormData.owner, phoneNumber: e.target.value },
                })}
                required
              />
              <Input
                label="Địa Chỉ"
                value={medicalRecordFormData.owner.address}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  owner: { ...medicalRecordFormData.owner, address: e.target.value },
                })}
              />
              </div>

              <div className="mb-6 p-4 bg-orange-50 rounded-lg border border-orange-200">
                <div className="flex items-center gap-2 mb-4">
                  <svg className="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <h3 className="text-lg font-bold text-orange-800">Chi Tiết Bệnh Án</h3>
                </div>
              <Input
                label="Tình Trạng Sức Khỏe"
                value={medicalRecordFormData.recordDetails.healthCondition}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  recordDetails: { ...medicalRecordFormData.recordDetails, healthCondition: e.target.value },
                })}
              />
              <Input
                label="Tiền Sử Bệnh"
                value={medicalRecordFormData.recordDetails.medicalHistory}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  recordDetails: { ...medicalRecordFormData.recordDetails, medicalHistory: e.target.value },
                })}
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Ngày Cập Nhật
                </label>
                <input
                  type="datetime-local"
                  value={medicalRecordFormData.recordDetails.updatedDate}
                  onChange={(e) => setMedicalRecordFormData({
                    ...medicalRecordFormData,
                    recordDetails: { ...medicalRecordFormData.recordDetails, updatedDate: e.target.value },
                  })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>
              <Input
                label="Nhiệt Độ"
                type="text"
                value={medicalRecordFormData.recordDetails.temperature}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  recordDetails: { ...medicalRecordFormData.recordDetails, temperature: e.target.value },
                })}
              />
              <Input
                label="Vắc Xin"
                value={medicalRecordFormData.recordDetails.vaccines}
                onChange={(e) => setMedicalRecordFormData({
                  ...medicalRecordFormData,
                  recordDetails: { ...medicalRecordFormData.recordDetails, vaccines: e.target.value },
                })}
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Kết Quả Chẩn Đoán
                </label>
                <textarea
                  value={medicalRecordFormData.recordDetails.diagnosisResult}
                  onChange={(e) => setMedicalRecordFormData({
                    ...medicalRecordFormData,
                    recordDetails: { ...medicalRecordFormData.recordDetails, diagnosisResult: e.target.value },
                  })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  rows={3}
                />
              </div>
              </div>
            </form>
          </Modal>

          {/* Medical Record Details Modal */}
          <Modal
            isOpen={showMedicalRecordDetailsModal}
            onClose={() => {
              setShowMedicalRecordDetailsModal(false);
              setSelectedMedicalRecord(null);
              setMedicalRecordDetails([]);
            }}
            title={
              <div className="flex items-center gap-3">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                <span>Chi Tiết Bệnh Án</span>
              </div>
            }
            footer={
              <Button
                variant="secondary"
                onClick={() => {
                  setShowMedicalRecordDetailsModal(false);
                  setSelectedMedicalRecord(null);
                  setMedicalRecordDetails([]);
                }}
              >
                Đóng
              </Button>
            }
          >
            {medicalRecordLoading ? (
              <div className="text-center py-12">
                <svg className="animate-spin h-8 w-8 text-primary-600 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p className="text-gray-500">Đang tải...</p>
              </div>
            ) : medicalRecordDetails.length === 0 ? (
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p className="text-gray-500 mb-4 text-lg">Chưa có chi tiết bệnh án</p>
                <p className="text-gray-400 text-sm mb-6">Bệnh án này chưa có thông tin khám bệnh</p>
                <Button
                  variant="primary"
                  onClick={() => {
                    // Có thể thêm tính năng tạo chi tiết mới ở đây
                    alert('Tính năng tạo chi tiết mới sẽ được thêm sau');
                  }}
                >
                  Tạo Chi Tiết Mới
                </Button>
              </div>
            ) : (
              <div className="space-y-4 max-h-[70vh] overflow-y-auto">
                {medicalRecordDetails
                  .sort((a, b) => new Date(b.updatedDate || 0) - new Date(a.updatedDate || 0))
                  .map((detail, index) => (
                    <Card 
                      key={detail.id || index} 
                      className={`p-5 transition hover:shadow-md ${
                        index === 0 ? 'border-l-4 border-green-500 bg-green-50' : 'border-l-4 border-blue-500'
                      }`}
                    >
                      <div className="flex justify-between items-start mb-4">
                        <div className="flex items-center gap-3">
                          <div className={`p-2 rounded-lg ${
                            index === 0 ? 'bg-green-100' : 'bg-blue-100'
                          }`}>
                            <svg className={`w-5 h-5 ${index === 0 ? 'text-green-600' : 'text-blue-600'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                            </svg>
                          </div>
                          <div>
                            <h4 className="font-bold text-lg text-gray-800">
                              Lần Khám #{medicalRecordDetails.length - index}
                              {index === 0 && (
                                <span className="ml-2 px-2 py-1 bg-green-100 text-green-700 text-xs rounded-full">Mới nhất</span>
                              )}
                            </h4>
                            {detail.updatedDate && (
                              <div className="flex items-center gap-1 text-sm text-gray-500 mt-1">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                {new Date(detail.updatedDate).toLocaleString('vi-VN', {
                                  year: 'numeric',
                                  month: 'long',
                                  day: 'numeric',
                                  hour: '2-digit',
                                  minute: '2-digit',
                                })}
                              </div>
                            )}
                          </div>
                        </div>
                        <Button
                          variant="secondary"
                          className="text-sm px-3 py-1"
                          onClick={() => handleEditDetail(detail)}
                        >
                          <span className="flex items-center gap-1">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                            Sửa
                          </span>
                        </Button>
                      </div>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="bg-white p-3 rounded-lg border border-gray-200">
                          <div className="flex items-center gap-2 mb-2">
                            <svg className="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            <p className="font-semibold text-gray-700">Tình Trạng Sức Khỏe</p>
                          </div>
                          <p className="text-gray-900 font-medium">{detail.healthCondition || 'N/A'}</p>
                        </div>
                        <div className="bg-white p-3 rounded-lg border border-gray-200">
                          <div className="flex items-center gap-2 mb-2">
                            <svg className="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                            </svg>
                            <p className="font-semibold text-gray-700">Nhiệt Độ</p>
                          </div>
                          <p className="text-gray-900 font-medium text-lg">
                            {detail.temperature ? (
                              <span className={detail.temperature > 39 ? 'text-red-600' : detail.temperature > 38 ? 'text-orange-600' : 'text-green-600'}>
                                {detail.temperature}°C
                              </span>
                            ) : 'N/A'}
                          </p>
                        </div>
                        <div className="md:col-span-2 bg-white p-3 rounded-lg border border-gray-200">
                          <div className="flex items-center gap-2 mb-2">
                            <svg className="w-5 h-5 text-purple-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                            <p className="font-semibold text-gray-700">Tiền Sử Bệnh</p>
                          </div>
                          <p className="text-gray-900">{detail.medicalHistory || 'N/A'}</p>
                        </div>
                        <div className="md:col-span-2 bg-white p-3 rounded-lg border border-gray-200">
                          <div className="flex items-center gap-2 mb-2">
                            <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                            </svg>
                            <p className="font-semibold text-gray-700">Vắc Xin</p>
                          </div>
                          <p className="text-gray-900">{detail.vaccines || 'N/A'}</p>
                        </div>
                        <div className="md:col-span-2 bg-gradient-to-r from-blue-50 to-indigo-50 p-4 rounded-lg border border-blue-200">
                          <div className="flex items-center gap-2 mb-2">
                            <svg className="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                            </svg>
                            <p className="font-semibold text-gray-800">Kết Quả Chẩn Đoán</p>
                          </div>
                          <p className="text-gray-900 font-medium">{detail.diagnosisResult || 'N/A'}</p>
                        </div>
                      </div>
                    </Card>
                  ))}
              </div>
            )}
          </Modal>

          {/* Update Detail Modal */}
          <Modal
            isOpen={showUpdateDetailModal}
            onClose={() => {
              setShowUpdateDetailModal(false);
              setEditingDetail(null);
            }}
            title="Cập Nhật Chi Tiết Bệnh Án"
            footer={
              <>
                <Button
                  variant="secondary"
                  onClick={() => {
                    setShowUpdateDetailModal(false);
                    setEditingDetail(null);
                  }}
                >
                  Hủy
                </Button>
                <Button onClick={handleUpdateDetail} disabled={formLoading}>
                  {formLoading ? 'Đang cập nhật...' : 'Cập Nhật'}
                </Button>
              </>
            }
          >
            <form onSubmit={handleUpdateDetail}>
              <Input
                label="Tình Trạng Sức Khỏe"
                value={detailFormData.healthCondition}
                onChange={(e) => setDetailFormData({ ...detailFormData, healthCondition: e.target.value })}
              />
              <Input
                label="Tiền Sử Bệnh"
                value={detailFormData.medicalHistory}
                onChange={(e) => setDetailFormData({ ...detailFormData, medicalHistory: e.target.value })}
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Ngày Cập Nhật
                </label>
                <input
                  type="datetime-local"
                  value={detailFormData.updatedDate}
                  onChange={(e) => setDetailFormData({ ...detailFormData, updatedDate: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>
              <Input
                label="Nhiệt Độ"
                type="number"
                step="0.1"
                value={detailFormData.temperature}
                onChange={(e) => setDetailFormData({ ...detailFormData, temperature: e.target.value })}
              />
              <Input
                label="Vắc Xin"
                value={detailFormData.vaccines}
                onChange={(e) => setDetailFormData({ ...detailFormData, vaccines: e.target.value })}
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Kết Quả Chẩn Đoán
                </label>
                <textarea
                  value={detailFormData.diagnosisResult}
                  onChange={(e) => setDetailFormData({ ...detailFormData, diagnosisResult: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  rows={3}
                />
              </div>
            </form>
          </Modal>
        </>
      )}

      {/* Delivery Tab */}
      {activeTab === 'delivery' && (
        <div>
          {deliveryLoading ? (
            <Card>
              <div className="text-center py-12">
                <p className="text-gray-500">Đang tải đơn hàng...</p>
              </div>
            </Card>
          ) : deliveryOrders.length === 0 ? (
            <Card>
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Không có đơn hàng giao hàng</p>
                <p className="text-gray-400 text-sm">Tất cả đơn hàng đã được giao hoặc không có đơn hàng nào cần giao</p>
              </div>
            </Card>
          ) : (
            <div className="space-y-4">
              {deliveryOrders.map((order) => (
                <Card key={order.orderNumber} className="border-l-4 border-blue-500">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-xl font-bold mb-2">
                        Đơn hàng #{order.orderNumber}
                      </h3>
                      <p className="text-sm text-gray-600">
                        Trạng thái: <span className={`font-semibold ${
                          order.orderStatus === 'PENDING' ? 'text-yellow-600' :
                          order.orderStatus === 'DELIVERING' ? 'text-blue-600' :
                          order.orderStatus === 'DELIVERED' ? 'text-green-600' :
                          'text-gray-600'
                        }`}>
                          {order.orderStatus === 'PENDING' ? 'Chờ giao hàng' :
                           order.orderStatus === 'DELIVERING' ? 'Đang giao hàng' :
                           order.orderStatus === 'DELIVERED' ? 'Đã giao hàng' :
                           order.orderStatus}
                        </span>
                      </p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-sm font-semibold ${
                      order.orderStatus === 'PENDING'
                        ? 'bg-yellow-100 text-yellow-800'
                        : order.orderStatus === 'DELIVERING'
                        ? 'bg-blue-100 text-blue-800'
                        : order.orderStatus === 'DELIVERED'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-gray-100 text-gray-800'
                    }`}>
                      {order.orderStatus === 'PENDING' ? 'Chờ giao hàng' :
                       order.orderStatus === 'DELIVERING' ? 'Đang giao hàng' :
                       order.orderStatus === 'DELIVERED' ? 'Đã giao hàng' :
                       order.orderStatus}
                    </span>
                  </div>

                  <div className="flex gap-2">
                    {order.orderStatus === 'PENDING' && (
                      <Button
                        variant="primary"
                        onClick={() => handleOpenDeliveryModal(order)}
                      >
                        Chọn Giao Hàng
                      </Button>
                    )}
                    
                    {order.orderStatus === 'DELIVERING' && (
                      <Button
                        variant="success"
                        onClick={() => handleUpdateToDelivered(order.orderNumber)}
                      >
                        Hoàn Thành Giao Hàng
                      </Button>
                    )}

                    {order.orderStatus === 'DELIVERED' && (
                      <Button variant="secondary" disabled>
                        ✓ Đã Giao Hàng
                      </Button>
                    )}
                  </div>
                </Card>
              ))}

              {/* Pagination */}
              {deliveryTotalPages > 1 && (
                <div className="flex justify-center gap-2 mt-6">
                  <Button
                    variant="secondary"
                    onClick={() => setDeliveryPage((p) => Math.max(0, p - 1))}
                    disabled={deliveryPage === 0}
                  >
                    Trước
                  </Button>
                  <span className="px-4 py-2">
                    Trang {deliveryPage + 1} / {deliveryTotalPages}
                  </span>
                  <Button
                    variant="secondary"
                    onClick={() => setDeliveryPage((p) => Math.min(deliveryTotalPages - 1, p + 1))}
                    disabled={deliveryPage >= deliveryTotalPages - 1}
                  >
                    Sau
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* Appointments Tab */}
      {activeTab === 'appointments' && (
        <div>
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
                <p className="text-gray-500 text-lg mb-2">Không có lịch hẹn</p>
                <p className="text-gray-400 text-sm">Chưa có lịch hẹn nào được tạo</p>
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
                          <p className="text-gray-600">Số điện thoại khách hàng:</p>
                          <p className="font-semibold">{appointment.customer?.phoneNumber || 'N/A'}</p>
                        </div>
                        <div>
                          <p className="text-gray-600">Thời gian:</p>
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
                        {appointment.description && (
                          <div>
                            <p className="text-gray-600">Mô tả:</p>
                            <p className="font-semibold">{appointment.description}</p>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <Button
                      variant="secondary"
                      onClick={() => handleEditAppointment(appointment)}
                    >
                      Chỉnh Sửa
                    </Button>
                  </div>
                </Card>
              ))}

              {/* Pagination */}
              {appointmentTotalPages > 1 && (
                <div className="flex justify-center gap-2 mt-6">
                  <Button
                    variant="secondary"
                    onClick={() => setAppointmentPage((p) => Math.max(0, p - 1))}
                    disabled={appointmentPage === 0}
                  >
                    Trước
                  </Button>
                  <span className="px-4 py-2">
                    Trang {appointmentPage + 1} / {appointmentTotalPages}
                  </span>
                  <Button
                    variant="secondary"
                    onClick={() => setAppointmentPage((p) => Math.min(appointmentTotalPages - 1, p + 1))}
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

      {/* Categories Tab */}
      {activeTab === 'categories' && (
        <div>
          {categoryLoading ? (
            <Card>
              <div className="text-center py-12">
                <p className="text-gray-500">Đang tải danh mục...</p>
              </div>
            </Card>
          ) : categories.length === 0 ? (
            <Card>
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Không có danh mục</p>
                <p className="text-gray-400 text-sm">Chưa có danh mục nào được tạo</p>
              </div>
            </Card>
          ) : (
            <>
              <Card className="overflow-x-auto mb-6">
                <table className="w-full">
                  <thead>
                    <tr className="border-b">
                      <th className="text-left p-4">Tên Danh Mục</th>
                      <th className="text-left p-4">Mô Tả</th>
                      <th className="text-left p-4">Hành Động</th>
                    </tr>
                  </thead>
                  <tbody>
                    {categories.map((category) => (
                      <tr key={category.id} className="border-b hover:bg-gray-50">
                        <td className="p-4 font-semibold">{category.name || 'N/A'}</td>
                        <td className="p-4">{category.description || 'N/A'}</td>
                        <td className="p-4">
                          <Button
                            variant="secondary"
                            onClick={() => handleEditCategory(category)}
                            disabled={editingCategory?.id === category.id}
                          >
                            Sửa
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </Card>

              {/* Pagination */}
              {categoryTotalPages > 1 && (
                <div className="flex justify-center gap-2 mt-6">
                  <Button
                    variant="secondary"
                    onClick={() => setCategoryPage((p) => Math.max(0, p - 1))}
                    disabled={categoryPage === 0}
                  >
                    Trước
                  </Button>
                  <span className="px-4 py-2">
                    Trang {categoryPage + 1} / {categoryTotalPages}
                  </span>
                  <Button
                    variant="secondary"
                    onClick={() => setCategoryPage((p) => Math.min(categoryTotalPages - 1, p + 1))}
                    disabled={categoryPage >= categoryTotalPages - 1}
                  >
                    Sau
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      )}

      {/* Discounts Tab */}
      {activeTab === 'discounts' && (
        <div>
          {/* Filter by Status */}
          <Card className="mb-6">
            <div className="flex items-center gap-4 mb-4">
              <label className="text-sm font-semibold">Lọc theo trạng thái:</label>
              <select
                value={discountStatus}
                onChange={(e) => {
                  setDiscountStatus(e.target.value);
                  setDiscountPage(0);
                }}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="ACTIVE">Hoạt động</option>
                <option value="INACTIVE">Ngừng hoạt động</option>
                <option value="EXPIRED">Hết hạn</option>
              </select>
            </div>
          </Card>

          {discountLoading ? (
            <Card>
              <div className="text-center py-12">
                <p className="text-gray-500">Đang tải mã giảm giá...</p>
              </div>
            </Card>
          ) : discounts.length === 0 ? (
            <Card>
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">Không có mã giảm giá</p>
                <p className="text-gray-400 text-sm">Chưa có mã giảm giá nào với trạng thái này</p>
              </div>
            </Card>
          ) : (
            <>
              <Card className="mb-6">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {discounts.map((discount) => (
                    <Card key={discount.id} className="p-5 border-l-4 border-green-500">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <h3 className="text-lg font-bold text-gray-800">{discount.discountCode || 'N/A'}</h3>
                          <p className="text-sm text-gray-600">{discount.discountName || 'N/A'}</p>
                        </div>
                        <span className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-xs font-semibold">
                          -{discount.percent}%
                        </span>
                      </div>
                      
                      <div className="space-y-2 text-sm">
                        <div className="flex items-center gap-2">
                          <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                          </svg>
                          <span className="text-gray-600">
                            <strong>Bắt đầu:</strong> {new Date(discount.startDate).toLocaleDateString('vi-VN')}
                          </span>
                        </div>
                        <div className="flex items-center gap-2">
                          <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                          </svg>
                          <span className="text-gray-600">
                            <strong>Kết thúc:</strong> {new Date(discount.endDate).toLocaleDateString('vi-VN')}
                          </span>
                        </div>
                        {discount.totalLimit !== null && (
                          <div className="flex items-center gap-2">
                            <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                            </svg>
                            <span className="text-gray-600">
                              <strong>Giới hạn:</strong> {discount.totalLimit} mã
                            </span>
                          </div>
                        )}
                        <div className="flex items-center gap-2">
                          <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          <span className="text-gray-600">
                            <strong>Đã dùng:</strong> {discount.usedCount || 0} mã
                          </span>
                        </div>
                      </div>
                    </Card>
                  ))}
                </div>
              </Card>

              {/* Pagination */}
              {discountTotalPages > 1 && (
                <div className="flex justify-center gap-2 mt-6">
                  <Button
                    variant="secondary"
                    onClick={() => setDiscountPage((p) => Math.max(0, p - 1))}
                    disabled={discountPage === 0}
                  >
                    Trước
                  </Button>
                  <span className="px-4 py-2">
                    Trang {discountPage + 1} / {discountTotalPages}
                  </span>
                  <Button
                    variant="secondary"
                    onClick={() => setDiscountPage((p) => Math.min(discountTotalPages - 1, p + 1))}
                    disabled={discountPage >= discountTotalPages - 1}
                  >
                    Sau
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      )}

      {/* Delivery Modal */}
      <Modal
        isOpen={showDeliveryModal}
        onClose={handleCloseDeliveryModal}
        title={`Giao Hàng - ${selectedOrderForDelivery?.orderNumber || ''}`}
        footer={
          <>
            <Button
              variant="secondary"
              onClick={handleCloseDeliveryModal}
            >
              Hủy
            </Button>
            <Button 
              onClick={handleSubmitDelivery} 
              disabled={deliveryFormLoading}
            >
              {deliveryFormLoading ? 'Đang xử lý...' : 'Gửi Giao Hàng'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleSubmitDelivery} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Tên Người Gửi *</label>
            <Input
              type="text"
              name="senderName"
              placeholder="Nhập tên người gửi"
              value={deliveryFormData.senderName}
              onChange={handleDeliveryFormChange}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Số Điện Thoại Người Gửi *</label>
            <Input
              type="tel"
              name="senderPhone"
              placeholder="Nhập số điện thoại"
              value={deliveryFormData.senderPhone}
              onChange={handleDeliveryFormChange}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Địa Chỉ Người Gửi</label>
            <div className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
              <p className="text-gray-700">{SENDER_ADDRESS}</p>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold mb-1">Cao (cm) *</label>
              <Input
                type="number"
                name="height"
                placeholder="Chiều cao"
                value={deliveryFormData.height}
                onChange={handleDeliveryFormChange}
                required
              />
            </div>
            <div>
              <label className="block text-sm font-semibold mb-1">Nặng (g) *</label>
              <Input
                type="number"
                name="weight"
                placeholder="Cân nặng"
                value={deliveryFormData.weight}
                onChange={handleDeliveryFormChange}
                required
              />
            </div>
            <div>
              <label className="block text-sm font-semibold mb-1">Rộng (cm) *</label>
              <Input
                type="number"
                name="width"
                placeholder="Chiều rộng"
                value={deliveryFormData.width}
                onChange={handleDeliveryFormChange}
                required
              />
            </div>
            <div>
              <label className="block text-sm font-semibold mb-1">Dài (cm) *</label>
              <Input
                type="number"
                name="length"
                placeholder="Chiều dài"
                value={deliveryFormData.length}
                onChange={handleDeliveryFormChange}
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Ghi Chú Bắt Buộc *</label>
            <select
              name="requiredNote"
              value={deliveryFormData.requiredNote}
              onChange={handleDeliveryFormChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="CHOTHUHANG">Cho Thử Hàng</option>
              <option value="CHOXEMHANGKHONTHU">Cho Xem Hàng Không Thử</option>
              <option value="KHONGCHOXEMHANG">Không Cho Xem Hàng</option>
            </select>
          </div>
        </form>
      </Modal>

      {/* Create Appointment Modal */}
      <Modal
        isOpen={showCreateAppointmentModal}
        onClose={() => {
          setShowCreateAppointmentModal(false);
          resetAppointmentForm();
        }}
        title="Tạo Lịch Hẹn Mới"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowCreateAppointmentModal(false);
                resetAppointmentForm();
              }}
            >
              Hủy
            </Button>
            <Button 
              onClick={handleCreateAppointment} 
              disabled={appointmentFormLoading}
            >
              {appointmentFormLoading ? 'Đang tạo...' : 'Tạo Lịch Hẹn'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleCreateAppointment} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Số Điện Thoại Khách Hàng *</label>
            <Input
              type="tel"
              placeholder="Nhập số điện thoại khách hàng"
              value={appointmentFormData.customerPhoneNumber}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, customerPhoneNumber: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Thời Gian Hẹn *</label>
            <Input
              type="datetime-local"
              value={appointmentFormData.appointmentTime}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, appointmentTime: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Trạng Thái *</label>
            <select
              value={appointmentFormData.status}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, status: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="SCHEDULED">Đã lên lịch</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Mô Tả</label>
            <textarea
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              placeholder="Nhập mô tả lịch hẹn"
              value={appointmentFormData.description}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, description: e.target.value })}
              rows={3}
            />
          </div>
        </form>
      </Modal>

      {/* Update Appointment Modal */}
      <Modal
        isOpen={showUpdateAppointmentModal}
        onClose={() => {
          setShowUpdateAppointmentModal(false);
          setEditingAppointment(null);
          resetAppointmentForm();
        }}
        title="Chỉnh Sửa Lịch Hẹn"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowUpdateAppointmentModal(false);
                setEditingAppointment(null);
                resetAppointmentForm();
              }}
            >
              Hủy
            </Button>
            <Button 
              onClick={handleUpdateAppointment} 
              disabled={appointmentFormLoading}
            >
              {appointmentFormLoading ? 'Đang cập nhật...' : 'Cập Nhật'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleUpdateAppointment} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Thời Gian Hẹn *</label>
            <Input
              type="datetime-local"
              value={appointmentFormData.appointmentTime}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, appointmentTime: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Trạng Thái *</label>
            <select
              value={appointmentFormData.status}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, status: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="SCHEDULED">Đã lên lịch</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Mô Tả</label>
            <textarea
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              placeholder="Nhập mô tả lịch hẹn"
              value={appointmentFormData.description}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, description: e.target.value })}
              rows={3}
            />
          </div>
        </form>
      </Modal>

      {/* Create Category Modal */}
      <Modal
        isOpen={showCreateCategoryModal && !editingCategory}
        onClose={() => {
          setShowCreateCategoryModal(false);
          resetCategoryForm();
        }}
        title="Tạo Danh Mục Mới"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowCreateCategoryModal(false);
                resetCategoryForm();
              }}
            >
              Hủy
            </Button>
            <Button 
              onClick={handleCreateCategory} 
              disabled={categoryFormLoading}
            >
              {categoryFormLoading ? 'Đang tạo...' : 'Tạo Danh Mục'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleCreateCategory} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Tên Danh Mục *</label>
            <Input
              type="text"
              placeholder="Ví dụ: Thức Ăn"
              value={categoryFormData.name}
              onChange={(e) => setCategoryFormData({ ...categoryFormData, name: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Mô Tả</label>
            <textarea
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              placeholder="Ví dụ: Thức ăn cho thú cưng"
              value={categoryFormData.description}
              onChange={(e) => setCategoryFormData({ ...categoryFormData, description: e.target.value })}
              rows={3}
            />
          </div>
        </form>
      </Modal>

      {/* Update Category Modal */}
      <Modal
        isOpen={!!editingCategory}
        onClose={() => {
          setEditingCategory(null);
          resetCategoryForm();
        }}
        title="Chỉnh Sửa Danh Mục"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setEditingCategory(null);
                resetCategoryForm();
              }}
            >
              Hủy
            </Button>
            <Button 
              onClick={handleUpdateCategory} 
              disabled={categoryFormLoading}
            >
              {categoryFormLoading ? 'Đang cập nhật...' : 'Cập Nhật'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleUpdateCategory} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Tên Danh Mục *</label>
            <Input
              type="text"
              placeholder="Ví dụ: Thức Ăn"
              value={categoryFormData.name}
              onChange={(e) => setCategoryFormData({ ...categoryFormData, name: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Mô Tả</label>
            <textarea
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              placeholder="Ví dụ: Thức ăn cho thú cưng"
              value={categoryFormData.description}
              onChange={(e) => setCategoryFormData({ ...categoryFormData, description: e.target.value })}
              rows={3}
            />
          </div>
        </form>
      </Modal>

      {/* Create Manual Discount Modal */}
      <Modal
        isOpen={showCreateManualDiscountModal}
        onClose={() => {
          setShowCreateManualDiscountModal(false);
          resetManualDiscountForm();
        }}
        title="Tạo Mã Giảm Giá Thủ Công"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowCreateManualDiscountModal(false);
                resetManualDiscountForm();
              }}
            >
              Hủy
            </Button>
            <Button 
              onClick={handleCreateManualDiscount} 
              disabled={discountFormLoading}
            >
              {discountFormLoading ? 'Đang tạo...' : 'Tạo Mã'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleCreateManualDiscount} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Mã Giảm Giá *</label>
            <Input
              type="text"
              placeholder="Ví dụ: BLACKFRIDAY30"
              value={manualDiscountFormData.discountCode}
              onChange={(e) => setManualDiscountFormData({ ...manualDiscountFormData, discountCode: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Tên Mã Giảm Giá *</label>
            <Input
              type="text"
              placeholder="Ví dụ: BLACKFRIDAY"
              value={manualDiscountFormData.discountName}
              onChange={(e) => setManualDiscountFormData({ ...manualDiscountFormData, discountName: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Phần Trăm Giảm Giá (%) *</label>
            <Input
              type="number"
              step="0.1"
              min="0"
              max="100"
              placeholder="Ví dụ: 30"
              value={manualDiscountFormData.percent}
              onChange={(e) => setManualDiscountFormData({ ...manualDiscountFormData, percent: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Ngày Bắt Đầu *</label>
            <Input
              type="datetime-local"
              value={manualDiscountFormData.startDate}
              onChange={(e) => setManualDiscountFormData({ ...manualDiscountFormData, startDate: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Ngày Kết Thúc *</label>
            <Input
              type="datetime-local"
              value={manualDiscountFormData.endDate}
              onChange={(e) => setManualDiscountFormData({ ...manualDiscountFormData, endDate: e.target.value })}
              required
            />
          </div>
        </form>
      </Modal>

      {/* Create Auto Discount Modal */}
      <Modal
        isOpen={showCreateAutoDiscountModal}
        onClose={() => {
          setShowCreateAutoDiscountModal(false);
          resetAutoDiscountForm();
        }}
        title="Tạo Mã Giảm Giá Tự Động"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() => {
                setShowCreateAutoDiscountModal(false);
                resetAutoDiscountForm();
              }}
            >
              Hủy
            </Button>
            <Button 
              onClick={handleCreateAutoDiscount} 
              disabled={discountFormLoading}
            >
              {discountFormLoading ? 'Đang tạo...' : 'Tạo Mã'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleCreateAutoDiscount} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Tên Mã Giảm Giá *</label>
            <Input
              type="text"
              placeholder="Ví dụ: Anniversary 1st"
              value={autoDiscountFormData.discountName}
              onChange={(e) => setAutoDiscountFormData({ ...autoDiscountFormData, discountName: e.target.value })}
              required
            />
            <p className="text-xs text-gray-500 mt-1">Mã giảm giá sẽ được tạo tự động</p>
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Phần Trăm Giảm Giá (%) *</label>
            <Input
              type="number"
              step="0.1"
              min="0"
              max="100"
              placeholder="Ví dụ: 25"
              value={autoDiscountFormData.percent}
              onChange={(e) => setAutoDiscountFormData({ ...autoDiscountFormData, percent: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Ngày Bắt Đầu *</label>
            <Input
              type="datetime-local"
              value={autoDiscountFormData.startDate}
              onChange={(e) => setAutoDiscountFormData({ ...autoDiscountFormData, startDate: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Ngày Kết Thúc *</label>
            <Input
              type="datetime-local"
              value={autoDiscountFormData.endDate}
              onChange={(e) => setAutoDiscountFormData({ ...autoDiscountFormData, endDate: e.target.value })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Giới Hạn Số Lượng (Tùy chọn)</label>
            <Input
              type="number"
              min="1"
              placeholder="Ví dụ: 100"
              value={autoDiscountFormData.totalLimit}
              onChange={(e) => setAutoDiscountFormData({ ...autoDiscountFormData, totalLimit: e.target.value })}
            />
            <p className="text-xs text-gray-500 mt-1">Để trống nếu không giới hạn số lượng sử dụng</p>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default EmployeePage;

