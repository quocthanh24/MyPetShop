import { useState } from 'react';
import { Card, Button, Input, ProductImage } from '@/components';

/**
 * ProductTab - Hiển thị danh sách sản phẩm
 * @param {Array} products - Danh sách sản phẩm
 * @param {Array} categories - Danh sách category
 * @param {boolean} loading - Loading state
 * @param {Function} onAddToCart - Callback add to cart
 * @param {Function} onBuyNow - Callback buy now
 * @param {Array} searchResults - Search results
 * @param {Function} onSearch - Search handler
 * @param {Function} onCategorySelect - Category filter handler
 */
const ProductTab = ({
  products,
  categories = [],
  loading = false,
  onAddToCart,
  onBuyNow,
  onSearch,
  onCategorySelect,
  currentCategory = '',
  currentSearchKeyword = '',
}) => {
  const [localSearchKeyword, setLocalSearchKeyword] = useState(currentSearchKeyword);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (onSearch) {
      onSearch(localSearchKeyword);
    }
  };

  const handleClearSearch = () => {
    setLocalSearchKeyword('');
    if (onSearch) {
      onSearch('');
    }
  };

  if (loading) {
    return (
      <Card>
        <div className="text-center py-12">
          <p className="text-gray-500">Đang tải sản phẩm...</p>
        </div>
      </Card>
    );
  }

  if (products.length === 0) {
    return (
      <Card>
        <div className="text-center py-12">
          <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
          <p className="text-gray-500 text-lg mb-2">Không tìm thấy sản phẩm</p>
        </div>
      </Card>
    );
  }

  return (
    <div>
      {/* Search & Filter Section */}
      <Card className="mb-6">
        <form onSubmit={handleSearchSubmit} className="mb-4">
          <div className="flex gap-4">
            <div className="flex-1">
              <Input
                type="text"
                placeholder="Tìm kiếm sản phẩm..."
                value={localSearchKeyword}
                onChange={(e) => setLocalSearchKeyword(e.target.value)}
              />
            </div>
            <Button type="submit">
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              Tìm Kiếm
            </Button>
            {localSearchKeyword && (
              <Button variant="secondary" onClick={handleClearSearch}>
                Xóa
              </Button>
            )}
          </div>
        </form>

        {/* Categories Filter */}
        {categories.length > 0 && (
          <div className="flex flex-wrap gap-2">
            <Button
              variant={currentCategory === '' ? 'primary' : 'secondary'}
              onClick={() => onCategorySelect && onCategorySelect('')}
            >
              Tất Cả
            </Button>
            {categories.map((category) => (
              <Button
                key={category}
                variant={currentCategory === category ? 'primary' : 'secondary'}
                onClick={() => onCategorySelect && onCategorySelect(category)}
              >
                {category}
              </Button>
            ))}
          </div>
        )}
      </Card>

      {/* Products Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
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
                {product.categoryName && (
                  <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs font-semibold">
                    {product.categoryName}
                  </span>
                )}
                {product.petType && (
                  <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs">
                    {product.petType}
                  </span>
                )}
              </div>

              <p className="text-blue-600 font-bold text-xl mb-3">
                {product.price?.toLocaleString('vi-VN')} VNĐ
              </p>

              <p className="text-sm text-gray-600 mb-3">
                Còn lại: <span className="font-semibold">{product.stockQuantity || 0}</span>
              </p>

              <div className="space-y-2">
                <Button
                  onClick={() => onAddToCart && onAddToCart(product.id)}
                  disabled={product.status !== 'ACTIVE' || product.stockQuantity <= 0}
                  className="w-full"
                >
                  <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  Thêm Vào Giỏ
                </Button>

                <Button
                  onClick={() => onBuyNow && onBuyNow(product)}
                  disabled={product.status !== 'ACTIVE' || product.stockQuantity <= 0}
                  className="w-full"
                  variant="secondary"
                >
                  Mua Ngay
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default ProductTab;
