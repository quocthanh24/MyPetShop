import { useState, useEffect } from 'react';
import { categoryAPI } from '../services/api';
import Card from '../components/Card';
import Input from '../components/Input';
import Button from '../components/Button';
import { useAuth } from '../context/AuthContext';

const CategoryPage = () => {
  const { user } = useAuth();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
  });
  const [formLoading, setFormLoading] = useState(false);

  useEffect(() => {
    loadCategories();
  }, [page]);

  const loadCategories = async () => {
    try {
      setLoading(true);
      const response = await categoryAPI.getAll(page, 10);
      console.log('Categories response:', response.data);
      
      const data = response.data?.content || [];
      setCategories(data);
      
      // Handle pagination info
      if (response.data?.page?.totalPages !== undefined) {
        setTotalPages(response.data.page.totalPages);
      } else if (response.data?.totalPages !== undefined) {
        setTotalPages(response.data.totalPages);
      } else {
        setTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading categories:', error);
      console.error('Error response:', error.response);
      if (error.response?.status === 403) {
        alert('Bạn không có quyền truy cập trang này. Vui lòng đăng nhập với tài khoản Employee.');
      } else if (error.response?.status === 401) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
      } else {
        alert('Có lỗi xảy ra khi tải danh sách danh mục');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCreateCategory = async (e) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      alert('Vui lòng nhập tên danh mục');
      return;
    }

    try {
      setFormLoading(true);
      await categoryAPI.create({
        name: formData.name.trim(),
        description: formData.description.trim() || '',
      });
      alert('Đã tạo danh mục thành công!');
      setFormData({ name: '', description: '' });
      setShowCreateForm(false);
      loadCategories(); // Reload categories
    } catch (error) {
      console.error('Error creating category:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo danh mục');
      }
    } finally {
      setFormLoading(false);
    }
  };

  const handleEditCategory = (category) => {
    setEditingCategory(category);
    setFormData({
      name: category.name || '',
      description: category.description || '',
    });
    setShowCreateForm(false);
  };

  const handleCancelEdit = () => {
    setEditingCategory(null);
    setFormData({ name: '', description: '' });
  };

  const handleUpdateCategory = async (e) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      alert('Vui lòng nhập tên danh mục');
      return;
    }

    if (!editingCategory) {
      return;
    }

    try {
      setFormLoading(true);
      await categoryAPI.update(editingCategory.id, {
        name: formData.name.trim(),
        description: formData.description.trim() || '',
      });
      alert('Đã cập nhật danh mục thành công!');
      setFormData({ name: '', description: '' });
      setEditingCategory(null);
      loadCategories(); // Reload categories
    } catch (error) {
      console.error('Error updating category:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi cập nhật danh mục');
      }
    } finally {
      setFormLoading(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Quản Lý Thể Loại</h1>
        <Button
          onClick={() => {
            if (editingCategory) {
              handleCancelEdit();
            }
            setShowCreateForm(!showCreateForm);
          }}
          variant={showCreateForm && !editingCategory ? 'secondary' : 'primary'}
          disabled={!!editingCategory}
        >
          {showCreateForm && !editingCategory ? 'Đóng' : 'Tạo Danh Mục Mới'}
        </Button>
      </div>

      {/* Create Category Form */}
      {showCreateForm && !editingCategory && (
        <Card className="mb-8">
          <h2 className="text-2xl font-bold mb-4">Tạo Thể Loại Mới</h2>
          <form onSubmit={handleCreateCategory}>
            <div className="mb-4">
              <Input
                label="Tên Thể Loại"
                placeholder="Ví dụ: Thức Ăn"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Mô Tả
              </label>
              <textarea
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder="Ví dụ: Thức ăn cho thú cưng"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={3}
              />
            </div>
            <div className="flex gap-2">
              <Button type="submit" disabled={formLoading}>
                {formLoading ? 'Đang tạo...' : 'Tạo Danh Mục'}
              </Button>
              <Button
                type="button"
                variant="secondary"
                onClick={() => {
                  setShowCreateForm(false);
                  setFormData({ name: '', description: '' });
                }}
              >
                Hủy
              </Button>
            </div>
          </form>
        </Card>
      )}

      {/* Edit Category Form */}
      {editingCategory && (
        <Card className="mb-8">
          <h2 className="text-2xl font-bold mb-4">Chỉnh Sửa Thể Loại</h2>
          <form onSubmit={handleUpdateCategory}>
            <div className="mb-4">
              <Input
                label="Tên Danh Mục"
                placeholder="Ví dụ: Thức Ăn"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Mô Tả
              </label>
              <textarea
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder="Ví dụ: Thức ăn cho thú cưng"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={3}
              />
            </div>
            <div className="flex gap-2">
              <Button type="submit" disabled={formLoading}>
                {formLoading ? 'Đang cập nhật...' : 'Cập Nhật'}
              </Button>
              <Button
                type="button"
                variant="secondary"
                onClick={handleCancelEdit}
              >
                Hủy
              </Button>
            </div>
          </form>
        </Card>
      )}

      {/* Categories List */}
      {loading ? (
        <div className="text-center py-12">
          <p className="text-gray-500">Đang tải...</p>
        </div>
      ) : (
        <>
          <Card className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left p-4">Tên Thể Loại</th>
                  <th className="text-left p-4">Mô Tả</th>
                  <th className="text-left p-4">Hành Động</th>
                </tr>
              </thead>
              <tbody>
                {categories.length === 0 ? (
                  <tr>
                    <td colSpan="3" className="text-center p-8 text-gray-500">
                      Không có danh mục nào
                    </td>
                  </tr>
                ) : (
                  categories.map((category) => (
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
                  ))
                )}
              </tbody>
            </table>
          </Card>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-6">
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

export default CategoryPage;

