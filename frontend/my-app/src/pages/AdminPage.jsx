import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    loadUsers();
  }, [page]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      
      // Debug: Check token
      const token = localStorage.getItem('accessToken');
      console.log('Token exists:', !!token);
      console.log('Token (first 20 chars):', token?.substring(0, 20));
      
      const response = await adminAPI.getAllUsers(page, 10);
      console.log('Full response:', response); // Debug log
      console.log('Response data:', response.data); // Debug log
      
      // Handle response structure: { content: [...], page: {...} }
      const data = response.data?.content || [];
      console.log('Users data:', data); // Debug log
      setUsers(data);
      
      // Handle pagination info
      if (response.data?.page?.totalPages !== undefined) {
        setTotalPages(response.data.page.totalPages);
      } else if (response.data?.totalPages !== undefined) {
        setTotalPages(response.data.totalPages);
      } else {
        setTotalPages(1); // Default to 1 page if not found
      }
    } catch (error) {
      console.error('Error loading users:', error);
      console.error('Error response:', error.response); // Debug log
      console.error('Error message:', error.message); // Debug log
      console.error('Error status:', error.response?.status); // Debug log
      console.error('Error data:', error.response?.data); // Debug log
      
      if (error.response?.status === 401) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        // Will be redirected by interceptor
      } else if (error.response?.status === 403) {
        alert('Bạn không có quyền truy cập trang này. Vui lòng đăng nhập với tài khoản Admin.');
      } else if (error.response?.status === 404) {
        alert('Không tìm thấy endpoint. Vui lòng kiểm tra lại API.');
      } else if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else if (error.message) {
        alert(`Lỗi: ${error.message}`);
      } else {
        alert('Có lỗi xảy ra khi tải danh sách người dùng. Vui lòng kiểm tra console để biết thêm chi tiết.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handlePromoteToEmployee = async (userId) => {
    if (!window.confirm('Bạn có chắc chắn muốn cấp quyền employee cho user này?')) {
      return;
    }

    try {
      await adminAPI.promoteToEmployee(userId);
      alert('Đã cấp quyền employee thành công!');
      loadUsers();
    } catch (error) {
      console.error('Error promoting user:', error);
      alert('Có lỗi xảy ra khi cấp quyền');
    }
  };

  const filteredUsers = users.filter((user) => {
    if (!searchKeyword) return true;
    const keyword = searchKeyword.toLowerCase();
    return (
      user.fullName?.toLowerCase().includes(keyword) ||
      user.phoneNumber?.toLowerCase().includes(keyword)
    );
  });


  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">Quản Lý Người Dùng</h1>

      {/* Search */}
      <div className="mb-6">
        <Input
          placeholder="Tìm kiếm theo tên, số điện thoại..."
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
        />
      </div>

      {/* Users Table */}
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
                  <th className="text-left p-4">Tên</th>
                  <th className="text-left p-4">Số điện thoại</th>
                  <th className="text-left p-4">Vai trò</th>
                  <th className="text-left p-4">Hành động</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="text-center p-8 text-gray-500">
                      Không tìm thấy người dùng nào
                    </td>
                  </tr>
                ) : (
                  filteredUsers.map((u) => (
                    <tr key={u.id} className="border-b hover:bg-gray-50">
                      <td className="p-4">{u.fullName || 'N/A'}</td>
                      <td className="p-4">{u.phoneNumber || 'N/A'}</td>
                      <td className="p-4">
                        <span
                          className={`px-2 py-1 rounded text-sm ${
                            u.role === 'ADMIN'
                              ? 'bg-red-100 text-red-800'
                              : u.role === 'EMPLOYEE'
                              ? 'bg-blue-100 text-blue-800'
                              : 'bg-gray-100 text-gray-800'
                          }`}
                        >
                          {u.role || 'CUSTOMER'}
                        </span>
                      </td>
                      <td className="p-4">
                        {u.role !== 'EMPLOYEE' && u.role !== 'ADMIN' && (
                          <Button
                            variant="success"
                            onClick={() => handlePromoteToEmployee(u.id)}
                          >
                            Cấp quyền Employee
                          </Button>
                        )}
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

export default AdminPage;

