import { useState, useEffect } from 'react';
import { productAPI, categoryAPI, cartAPI } from '../services/api';
import Card from '../components/Card';
import Input from '../components/Input';
import Button from '../components/Button';
import { useAuth } from '../context/AuthContext';

const ProductPage = () => {
  const { isAuthenticated, user } = useAuth();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadProducts();
    loadCategories();
  }, [page, selectedCategory]);

  const loadProducts = async () => {
    try {
      setLoading(true);
      let response;
      if (searchKeyword) {
        response = await productAPI.search(searchKeyword, page, 10);
      } else if (selectedCategory) {
        response = await productAPI.getByCategory(selectedCategory, page, 10);
      } else {
        response = await productAPI.getAll(page, 10);
      }
      
      console.log('Products response:', response.data);
      
      // Handle response structure: { content: [...], page: {...} }
      const data = response.data?.content || [];
      setProducts(data);
      
      // Handle pagination info
      if (response.data?.page?.totalPages !== undefined) {
        setTotalPages(response.data.page.totalPages);
      } else if (response.data?.totalPages !== undefined) {
        setTotalPages(response.data.totalPages);
      } else {
        setTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading products:', error);
      console.error('Error response:', error.response);
      console.error('Error message:', error.message);
      console.error('Error status:', error.response?.status);
      console.error('Error data:', error.response?.data);
      
      if (error.response?.status === 401) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        // Will be redirected by interceptor
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

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    loadProducts();
  };

  const handleAddToCart = async (productId, quantity = 1) => {
    if (!isAuthenticated) {
      alert('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng');
      return;
    }

    try {
      await cartAPI.addToCart({
        userId: user.userId,
        productId,
        quantity,
      });
      alert('Đã thêm sản phẩm vào giỏ hàng!');
    } catch (error) {
      console.error('Error adding to cart:', error);
      alert('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng');
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">Sản Phẩm</h1>

      {/* Search and Filter */}
      <div className="mb-8">
        <form onSubmit={handleSearch} className="flex gap-4 mb-4">
          <Input
            placeholder="Tìm kiếm sản phẩm..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            className="flex-1"
          />
          <Button type="submit">Tìm Kiếm</Button>
        </form>

        <div className="flex gap-2 flex-wrap">
          <Button
            variant={selectedCategory === '' ? 'primary' : 'secondary'}
            onClick={() => {
              setSelectedCategory('');
              setPage(0);
            }}
          >
            Tất Cả
          </Button>
          {categories.map((category) => (
            <Button
              key={category.id || category.name}
              variant={selectedCategory === category.name ? 'primary' : 'secondary'}
              onClick={() => {
                setSelectedCategory(category.name);
                setPage(0);
              }}
            >
              {category.name}
            </Button>
          ))}
        </div>
      </div>

      {/* Products Grid */}
      {loading ? (
        <div className="text-center py-12">
          <p className="text-gray-500">Đang tải...</p>
        </div>
      ) : products.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500">Không tìm thấy sản phẩm nào</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
            {products.map((product) => (
              <Card key={product.id}>
                <div className="h-48 bg-gray-200 rounded mb-4 flex items-center justify-center overflow-hidden relative">
                  {product.thumbnailUrl ? (
                    <img
                      src={product.thumbnailUrl}
                      alt={product.name}
                      className="h-full w-full object-cover rounded"
                      onError={(e) => {
                        e.target.style.display = 'none';
                        const placeholder = e.target.parentElement.querySelector('.image-placeholder');
                        if (placeholder) placeholder.style.display = 'flex';
                      }}
                    />
                  ) : null}
                  <span className="text-gray-400 image-placeholder" style={{ display: product.thumbnailUrl ? 'none' : 'flex' }}>
                    No Image
                  </span>
                </div>
                <h3 className="text-xl font-bold mb-2">{product.name || 'N/A'}</h3>
                
                <div className="mb-3">
                  <p className="text-primary-600 font-bold text-lg">
                    {product.price?.toLocaleString('vi-VN')} VNĐ
                  </p>
                </div>

                <div className="mb-3 space-y-1 text-sm">
                  <p className="text-gray-600">
                    <span className="font-semibold">Danh mục:</span> {product.categoryName || 'N/A'}
                  </p>
                  <p className="text-gray-600">
                    <span className="font-semibold">Loại thú cưng:</span> {product.petType || 'N/A'}
                  </p>
                  <p className="text-gray-600">
                    <span className="font-semibold">Tồn kho:</span> {product.stockQuantity || 0}
                  </p>
                  <div className="flex items-center gap-2">
                    <span className="font-semibold">Trạng thái:</span>
                    <span
                      className={`px-2 py-1 rounded text-xs ${
                        product.status === 'ACTIVE'
                          ? 'bg-green-100 text-green-800'
                          : product.status === 'INACTIVE'
                          ? 'bg-gray-100 text-gray-800'
                          : 'bg-red-100 text-red-800'
                      }`}
                    >
                      {product.status || 'N/A'}
                    </span>
                  </div>
                </div>

                {product.imageUrls && product.imageUrls.length > 0 && (
                  <div className="mb-3">
                    <p className="text-xs text-gray-500 mb-1">
                      {product.imageUrls.length} hình ảnh khác
                    </p>
                  </div>
                )}

                {user?.role === 'EMPLOYEE' ? (
                  <div className="w-full p-3 text-center bg-gray-100 text-gray-600 rounded border border-gray-300">
                    <p className="text-sm font-medium">Chỉ xem - Nhân viên không thể mua</p>
                  </div>
                ) : (
                  <Button
                    onClick={() => handleAddToCart(product.id, 1)}
                    className="w-full"
                    disabled={
                      !isAuthenticated || 
                      (product.stockQuantity || 0) === 0 ||
                      product.status === 'INACTIVE'
                    }
                  >
                    {product.status === 'INACTIVE' 
                      ? 'Sản phẩm không khả dụng' 
                      : (product.stockQuantity || 0) === 0
                      ? 'Hết hàng'
                      : 'Thêm Vào Giỏ Hàng'}
                  </Button>
                )}
              </Card>
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center gap-2">
              <Button
                variant="secondary"
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
              >
                Trước
              </Button>
              <span className="px-4 py-2">
                Trang {page + 1} / {totalPages}
              </span>
              <Button
                variant="secondary"
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
              >
                Sau
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ProductPage;

