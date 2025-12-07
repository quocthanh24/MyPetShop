import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { petAPI, medicalRecordAPI, employeeAppointmentAPI } from '../services/api';
import Card from '../components/Card';
import Input from '../components/Input';
import Button from '../components/Button';
import Modal from '../components/Modal';

const AppointmentPage = () => {
  const { isAuthenticated, user } = useAuth();
  const [petCode, setPetCode] = useState('');
  const [petInfo, setPetInfo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showCreatePetModal, setShowCreatePetModal] = useState(false);
  const [newPetData, setNewPetData] = useState({
    petCode: '',
    petName: '',
    petType: 'DOG',
    breed: '',
    age: '',
    weight: '',
    ownerPhoneNumber: '',
  });
  const [medicalRecords, setMedicalRecords] = useState([]);
  const [showRecordsModal, setShowRecordsModal] = useState(false);

  // Employee appointment management states
  const [appointments, setAppointments] = useState([]);
  const [appointmentPage, setAppointmentPage] = useState(0);
  const [appointmentTotalPages, setAppointmentTotalPages] = useState(0);
  const [appointmentLoading, setAppointmentLoading] = useState(false);
  const [showCreateAppointmentModal, setShowCreateAppointmentModal] = useState(false);
  const [showUpdateAppointmentModal, setShowUpdateAppointmentModal] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState(null);
  const [viewMode, setViewMode] = useState('week'); // 'all', 'week', or 'range'
  const [dateRange, setDateRange] = useState({
    startDate: '',
    endDate: '',
  });
  const [appointmentFormData, setAppointmentFormData] = useState({
    customerPhoneNumber: '',
    employeeId: '',
    appointmentTime: '',
    status: 'SCHEDULED',
    description: '',
  });

  const handleSearchPet = async (e) => {
    e.preventDefault();
    if (!petCode.trim()) {
      setError('Vui lòng nhập mã thú cưng');
      return;
    }

    setLoading(true);
    setError('');
    setPetInfo(null);

    try {
      const response = await petAPI.getByCode(petCode);
      setPetInfo(response.data);
    } catch (error) {
      console.error('Error searching pet:', error);
      setError('Không tìm thấy thú cưng với mã này');
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePet = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      alert('Vui lòng đăng nhập để tạo thú cưng');
      return;
    }

    setLoading(true);
    try {
      await petAPI.create(newPetData);
      alert('Tạo thú cưng thành công!');
      setShowCreatePetModal(false);
      setNewPetData({
        petCode: '',
        petName: '',
        petType: 'DOG',
        breed: '',
        age: '',
        weight: '',
        ownerPhoneNumber: '',
      });
    } catch (error) {
      console.error('Error creating pet:', error);
      alert('Có lỗi xảy ra khi tạo thú cưng');
    } finally {
      setLoading(false);
    }
  };

  const handleViewMedicalRecords = async (phoneNumber) => {
    try {
      const response = await medicalRecordAPI.getByPhoneNumber(phoneNumber);
      setMedicalRecords(response.data || []);
      setShowRecordsModal(true);
    } catch (error) {
      console.error('Error loading medical records:', error);
      alert('Không thể tải lịch sử khám bệnh');
    }
  };

  // Employee appointment management functions
  useEffect(() => {
    if (user?.role === 'EMPLOYEE' && user?.userId) {
      setAppointmentFormData(prev => ({
        ...prev,
        employeeId: user.userId,
      }));
    }
  }, [user]);

  useEffect(() => {
    if (user?.role === 'EMPLOYEE' && user?.userId) {
      if (viewMode === 'range' && (!dateRange.startDate || !dateRange.endDate)) {
        // Don't load if range mode but dates not selected
        return;
      }
      loadAppointments();
    }
  }, [user, appointmentPage, viewMode, dateRange.startDate, dateRange.endDate]);

  const loadAppointments = async () => {
    if (user?.role !== 'EMPLOYEE') return;
    
    try {
      setAppointmentLoading(true);
      // Load more appointments to filter properly
      const response = await employeeAppointmentAPI.getAll(appointmentPage, 100);
      
      let appointmentsData = response.data?.content || [];
      
      // Filter appointments based on view mode
      if (viewMode === 'week') {
        const now = new Date();
        const startOfWeek = new Date(now);
        startOfWeek.setDate(now.getDate() - now.getDay()); // Sunday
        startOfWeek.setHours(0, 0, 0, 0);
        
        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6); // Saturday
        endOfWeek.setHours(23, 59, 59, 999);
        
        appointmentsData = appointmentsData.filter(apt => {
          const aptDate = new Date(apt.appointmentTime);
          return aptDate >= startOfWeek && aptDate <= endOfWeek;
        });
      } else if (viewMode === 'range' && dateRange.startDate && dateRange.endDate) {
        const startDate = new Date(dateRange.startDate);
        startDate.setHours(0, 0, 0, 0);
        
        const endDate = new Date(dateRange.endDate);
        endDate.setHours(23, 59, 59, 999);
        
        appointmentsData = appointmentsData.filter(apt => {
          const aptDate = new Date(apt.appointmentTime);
          return aptDate >= startDate && aptDate <= endDate;
        });
      }
      
      setAppointments(appointmentsData);
      
      if (response.data?.totalPages !== undefined) {
        setAppointmentTotalPages(response.data.totalPages);
      } else {
        setAppointmentTotalPages(1);
      }
    } catch (error) {
      console.error('Error loading appointments:', error);
      if (error.response?.status === 401) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
      } else {
        alert('Có lỗi xảy ra khi tải danh sách lịch hẹn');
      }
      setAppointments([]);
    } finally {
      setAppointmentLoading(false);
    }
  };

  // Get week days (Sunday to Saturday)
  const getWeekDays = () => {
    const now = new Date();
    const startOfWeek = new Date(now);
    startOfWeek.setDate(now.getDate() - now.getDay()); // Sunday
    
    const days = [];
    for (let i = 0; i < 7; i++) {
      const day = new Date(startOfWeek);
      day.setDate(startOfWeek.getDate() + i);
      days.push(day);
    }
    return days;
  };

  // Get time slots (8:00 AM to 8:00 PM, every hour)
  const getTimeSlots = () => {
    const slots = [];
    for (let hour = 8; hour <= 20; hour++) {
      slots.push(`${hour.toString().padStart(2, '0')}:00`);
    }
    return slots;
  };

  // Get appointments for a specific day and time slot
  const getAppointmentsForSlot = (day, timeSlot) => {
    return appointments.filter(apt => {
      const aptDate = new Date(apt.appointmentTime);
      const aptDay = aptDate.toDateString();
      const aptTime = `${aptDate.getHours().toString().padStart(2, '0')}:00`;
      
      return aptDay === day.toDateString() && aptTime === timeSlot;
    });
  };

  // Group appointments by day
  const getAppointmentsByDay = () => {
    const grouped = {};
    appointments.forEach(apt => {
      const aptDate = new Date(apt.appointmentTime);
      const dayKey = aptDate.toDateString();
      
      if (!grouped[dayKey]) {
        grouped[dayKey] = [];
      }
      grouped[dayKey].push(apt);
    });
    return grouped;
  };

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
    if (!appointmentFormData.employeeId) {
      alert('Không tìm thấy thông tin nhân viên');
      return;
    }

    try {
      setAppointmentLoading(true);
      // Format datetime: convert from "YYYY-MM-DDTHH:mm" to "YYYY-MM-DDTHH:mm:00"
      const formattedDateTime = appointmentFormData.appointmentTime 
        ? `${appointmentFormData.appointmentTime}:00`
        : '';
      
      const appointmentData = {
        customerPhoneNumber: appointmentFormData.customerPhoneNumber.trim(),
        employeeId: appointmentFormData.employeeId,
        appointmentTime: formattedDateTime,
        status: appointmentFormData.status,
        description: appointmentFormData.description.trim() || '',
      };
      
      await employeeAppointmentAPI.create(appointmentData);
      alert('Đã tạo lịch hẹn thành công!');
      setShowCreateAppointmentModal(false);
      setAppointmentFormData({
        customerPhoneNumber: '',
        employeeId: user?.userId || '',
        appointmentTime: '',
        status: 'SCHEDULED',
        description: '',
      });
      loadAppointments(); // Reload appointments list
    } catch (error) {
      console.error('Error creating appointment:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi tạo lịch hẹn');
      }
    } finally {
      setAppointmentLoading(false);
    }
  };

  const handleUpdateAppointment = async (e) => {
    e.preventDefault();
    
    if (!editingAppointment) return;
    if (!appointmentFormData.appointmentTime) {
      alert('Vui lòng chọn thời gian hẹn');
      return;
    }
    if (!appointmentFormData.employeeId) {
      alert('Không tìm thấy thông tin nhân viên');
      return;
    }

    try {
      setAppointmentLoading(true);
      // Format datetime: convert from "YYYY-MM-DDTHH:mm" to "YYYY-MM-DDTHH:mm:00"
      const formattedDateTime = appointmentFormData.appointmentTime 
        ? `${appointmentFormData.appointmentTime}:00`
        : '';
      
      const appointmentData = {
        employeeId: appointmentFormData.employeeId,
        appointmentTime: formattedDateTime,
        status: appointmentFormData.status,
        description: appointmentFormData.description.trim() || '',
      };
      
      await employeeAppointmentAPI.update(editingAppointment.id, appointmentData);
      alert('Đã cập nhật lịch hẹn thành công!');
      setShowUpdateAppointmentModal(false);
      setEditingAppointment(null);
      setAppointmentFormData({
        customerPhoneNumber: '',
        employeeId: user?.userId || '',
        appointmentTime: '',
        status: 'SCHEDULED',
        description: '',
      });
      loadAppointments(); // Reload appointments list
    } catch (error) {
      console.error('Error updating appointment:', error);
      if (error.response?.data?.message) {
        alert(`Lỗi: ${error.response.data.message}`);
      } else {
        alert('Có lỗi xảy ra khi cập nhật lịch hẹn');
      }
    } finally {
      setAppointmentLoading(false);
    }
  };

  const handleEditAppointment = (appointment) => {
    setEditingAppointment(appointment);
    setAppointmentFormData({
      customerPhoneNumber: appointment.customer?.phoneNumber || '',
      employeeId: user?.userId || appointment.employeeId || '',
      appointmentTime: appointment.appointmentTime ? new Date(appointment.appointmentTime).toISOString().slice(0, 16) : '',
      status: appointment.status || 'SCHEDULED',
      description: appointment.description || '',
    });
    setShowUpdateAppointmentModal(true);
  };

  // Check if user is employee
  const isEmployee = user?.role === 'EMPLOYEE';

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">Tìm Kiếm Lịch Hẹn</h1>

      {/* Employee Appointment Management Section */}
      {isEmployee && (
        <>
          <Card className="mb-8">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">Tìm Kiếm Lịch Hẹn</h2>
              <Button onClick={() => setShowCreateAppointmentModal(true)}>
                Tạo Lịch Hẹn Mới
              </Button>
            </div>
            
            {/* View Mode Tabs */}
            <div className="flex gap-2 mb-4 flex-wrap">
              <Button
                variant={viewMode === 'all' ? 'primary' : 'secondary'}
                onClick={() => {
                  setViewMode('all');
                  setAppointmentPage(0);
                }}
              >
                Tất Cả
              </Button>
              <Button
                variant={viewMode === 'week' ? 'primary' : 'secondary'}
                onClick={() => {
                  setViewMode('week');
                  setAppointmentPage(0);
                }}
              >
                Trong Tuần
              </Button>
              <Button
                variant={viewMode === 'range' ? 'primary' : 'secondary'}
                onClick={() => {
                  setViewMode('range');
                  setAppointmentPage(0);
                }}
              >
                Khoảng Thời Gian
              </Button>
            </div>

            {/* Date Range Picker for Range Mode */}
            {viewMode === 'range' && (
              <div className="flex gap-4 mb-4 p-4 bg-gray-50 rounded-lg">
                <div className="flex-1">
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Từ Ngày
                  </label>
                  <input
                    type="date"
                    value={dateRange.startDate}
                    onChange={(e) => {
                      setDateRange({ ...dateRange, startDate: e.target.value });
                    }}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  />
                </div>
                <div className="flex-1">
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Đến Ngày
                  </label>
                  <input
                    type="date"
                    value={dateRange.endDate}
                    onChange={(e) => {
                      setDateRange({ ...dateRange, endDate: e.target.value });
                    }}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  />
                </div>
                <div className="flex items-end">
                  <Button
                    onClick={() => {
                      if (dateRange.startDate && dateRange.endDate) {
                        loadAppointments();
                      } else {
                        alert('Vui lòng chọn cả ngày bắt đầu và ngày kết thúc');
                      }
                    }}
                  >
                    Áp Dụng
                  </Button>
                </div>
              </div>
            )}

            {/* Calendar View for Week Mode */}
            {viewMode === 'week' && !appointmentLoading && (
              <div className="mb-8">
                <div className="overflow-x-auto">
                  <div className="min-w-full">
                    {/* Week Header */}
                    <div className="grid grid-cols-8 border-b-2 border-gray-300">
                      <div className="p-4 font-bold text-gray-700 border-r border-gray-200">
                        Giờ
                      </div>
                      {getWeekDays().map((day, index) => (
                        <div
                          key={index}
                          className="p-4 text-center font-semibold border-r border-gray-200 last:border-r-0"
                        >
                          <div className="text-sm text-gray-600">
                            {['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'][index]}
                          </div>
                          <div className="text-lg">
                            {day.getDate()}/{day.getMonth() + 1}
                          </div>
                        </div>
                      ))}
                    </div>

                    {/* Time Slots */}
                    {getTimeSlots().map((timeSlot) => (
                      <div key={timeSlot} className="grid grid-cols-8 border-b border-gray-200 hover:bg-gray-50">
                        <div className="p-3 font-medium text-gray-600 border-r border-gray-200 text-sm">
                          {timeSlot}
                        </div>
                        {getWeekDays().map((day, dayIndex) => {
                          const slotAppointments = getAppointmentsForSlot(day, timeSlot);
                          return (
                            <div
                              key={dayIndex}
                              className="p-2 min-h-[80px] border-r border-gray-200 last:border-r-0"
                            >
                              {slotAppointments.map((apt) => (
                                <div
                                  key={apt.id}
                                  className={`mb-1 p-2 rounded text-xs cursor-pointer transition hover:shadow-md ${
                                    apt.status === 'SCHEDULED'
                                      ? 'bg-blue-100 border-l-4 border-blue-500 text-blue-900'
                                      : apt.status === 'COMPLETED'
                                      ? 'bg-green-100 border-l-4 border-green-500 text-green-900'
                                      : 'bg-gray-100 border-l-4 border-gray-500 text-gray-900'
                                  }`}
                                  onClick={() => handleEditAppointment(apt)}
                                  title={`${apt.customer?.fullName || apt.customer?.phoneNumber || 'N/A'} - ${apt.description || 'N/A'}`}
                                >
                                  <div className="font-semibold truncate">
                                    {new Date(apt.appointmentTime).toLocaleTimeString('vi-VN', {
                                      hour: '2-digit',
                                      minute: '2-digit',
                                    })}
                                  </div>
                                  <div className="truncate text-xs font-medium">
                                    {apt.customer?.fullName || apt.customer?.phoneNumber || 'N/A'}
                                  </div>
                                  {apt.customer?.phoneNumber && (
                                    <div className="truncate text-xs opacity-75">
                                      {apt.customer.phoneNumber}
                                    </div>
                                  )}
                                  {apt.description && (
                                    <div className="truncate text-xs opacity-75 mt-1">
                                      {apt.description}
                                    </div>
                                  )}
                                </div>
                              ))}
                            </div>
                          );
                        })}
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* List View for All and Range Mode */}
            {(viewMode === 'all' || viewMode === 'range') && (
              <>
                {appointmentLoading ? (
                  <div className="text-center py-8">
                    <p className="text-gray-500">Đang tải...</p>
                  </div>
                ) : appointments.length === 0 ? (
                  <div className="text-center py-8">
                    <p className="text-gray-500">
                      {viewMode === 'range' 
                        ? 'Không có lịch hẹn nào trong khoảng thời gian đã chọn' 
                        : 'Không có lịch hẹn nào'}
                    </p>
                  </div>
                ) : (
                  <>
                    {/* Grouped by Day View */}
                    {Object.entries(getAppointmentsByDay()).map(([dayKey, dayAppointments]) => {
                      const day = new Date(dayKey);
                      return (
                        <div key={dayKey} className="mb-6">
                          <h3 className="text-lg font-bold mb-3 text-gray-700 border-b pb-2">
                            {day.toLocaleDateString('vi-VN', {
                              weekday: 'long',
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric',
                            })}
                          </h3>
                          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                            {dayAppointments
                              .sort((a, b) => new Date(a.appointmentTime) - new Date(b.appointmentTime))
                              .map((appointment) => (
                                <Card
                                  key={appointment.id}
                                  className={`p-4 cursor-pointer transition hover:shadow-lg ${
                                    appointment.status === 'SCHEDULED'
                                      ? 'border-l-4 border-blue-500'
                                      : appointment.status === 'COMPLETED'
                                      ? 'border-l-4 border-green-500'
                                      : 'border-l-4 border-gray-500'
                                  }`}
                                  onClick={() => handleEditAppointment(appointment)}
                                >
                                  <div className="flex justify-between items-start mb-2">
                                    <div className="font-bold text-lg">
                                      {new Date(appointment.appointmentTime).toLocaleTimeString('vi-VN', {
                                        hour: '2-digit',
                                        minute: '2-digit',
                                      })}
                                    </div>
                                    <span
                                      className={`px-2 py-1 rounded text-xs ${
                                        appointment.status === 'SCHEDULED'
                                          ? 'bg-blue-100 text-blue-800'
                                          : appointment.status === 'COMPLETED'
                                          ? 'bg-green-100 text-green-800'
                                          : 'bg-gray-100 text-gray-800'
                                      }`}
                                    >
                                      {appointment.status || 'N/A'}
                                    </span>
                                  </div>
                                  <div className="text-sm text-gray-600 mb-1">
                                    <strong>Khách hàng:</strong> {appointment.customer?.fullName || appointment.customer?.phoneNumber || 'N/A'}
                                  </div>
                                  {appointment.customer?.phoneNumber && (
                                    <div className="text-sm text-gray-500 mb-1">
                                      <strong>SĐT:</strong> {appointment.customer.phoneNumber}
                                    </div>
                                  )}
                                  {appointment.customer?.gmail && (
                                    <div className="text-sm text-gray-500 mb-1">
                                      <strong>Email:</strong> {appointment.customer.gmail}
                                    </div>
                                  )}
                                  {appointment.description && (
                                    <div className="text-sm text-gray-500 mt-2">
                                      <strong>Mô tả:</strong> {appointment.description}
                                    </div>
                                  )}
                                  {appointment.acceptedBy && (
                                    <div className="text-xs text-gray-400 mt-2">
                                      Nhận bởi: {appointment.acceptedBy}
                                    </div>
                                  )}
                                  <div className="mt-3">
                                    <Button
                                      variant="secondary"
                                      className="text-sm px-3 py-1"
                                      onClick={(e) => {
                                        e.stopPropagation();
                                        handleEditAppointment(appointment);
                                      }}
                                    >
                                      Sửa
                                    </Button>
                                  </div>
                                </Card>
                              ))}
                          </div>
                        </div>
                      );
                    })}

                    {/* Pagination */}
                    {viewMode === 'all' && appointmentTotalPages > 1 && (
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
                  </>
                )}
              </>
            )}
          </Card>
        </>
      )}


      {/* Medical Records Modal */}
      <Modal
        isOpen={showRecordsModal}
        onClose={() => setShowRecordsModal(false)}
        title="Lịch Sử Khám Bệnh"
      >
        {medicalRecords.length === 0 ? (
          <p className="text-gray-500">Không có lịch sử khám bệnh</p>
        ) : (
          <div className="space-y-4">
            {medicalRecords.map((record, index) => (
              <Card key={index} className="p-4">
                <p><strong>Ngày:</strong> {new Date(record.date).toLocaleDateString('vi-VN')}</p>
                <p><strong>Chẩn đoán:</strong> {record.diagnosis || 'N/A'}</p>
                <p><strong>Ghi chú:</strong> {record.note || 'N/A'}</p>
              </Card>
            ))}
          </div>
        )}
      </Modal>

      {/* Create Appointment Modal for Employee */}
      {isEmployee && (
        <Modal
          isOpen={showCreateAppointmentModal}
          onClose={() => {
            setShowCreateAppointmentModal(false);
            setAppointmentFormData({
              customerPhoneNumber: '',
              employeeId: user?.userId || '',
              appointmentTime: '',
              status: 'SCHEDULED',
              description: '',
            });
          }}
          title="Tạo Lịch Hẹn Mới"
          footer={
            <>
              <Button
                variant="secondary"
                onClick={() => {
                  setShowCreateAppointmentModal(false);
                  setAppointmentFormData({
                    customerPhoneNumber: '',
                    employeeId: user?.userId || '',
                    appointmentTime: '',
                    status: 'SCHEDULED',
                    description: '',
                  });
                }}
              >
                Hủy
              </Button>
              <Button onClick={handleCreateAppointment} disabled={appointmentLoading}>
                {appointmentLoading ? 'Đang tạo...' : 'Tạo Lịch Hẹn'}
              </Button>
            </>
          }
        >
          <form onSubmit={handleCreateAppointment}>
            <Input
              label="Số Điện Thoại Khách Hàng"
              placeholder="Ví dụ: 0827563759"
              value={appointmentFormData.customerPhoneNumber}
              onChange={(e) => setAppointmentFormData({ ...appointmentFormData, customerPhoneNumber: e.target.value })}
              required
            />
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Thời Gian Hẹn <span className="text-red-500">*</span>
              </label>
              <input
                type="datetime-local"
                value={appointmentFormData.appointmentTime}
                onChange={(e) => setAppointmentFormData({ ...appointmentFormData, appointmentTime: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Trạng Thái <span className="text-red-500">*</span>
              </label>
              <select
                value={appointmentFormData.status}
                onChange={(e) => setAppointmentFormData({ ...appointmentFormData, status: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              >
                <option value="SCHEDULED">SCHEDULED</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Mô Tả
              </label>
              <textarea
                value={appointmentFormData.description}
                onChange={(e) => setAppointmentFormData({ ...appointmentFormData, description: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                rows={3}
                placeholder="Ví dụ: Tái khám lần 1 cho khách hàng"
              />
            </div>
          </form>
        </Modal>
      )}

      {/* Update Appointment Modal for Employee */}
      {isEmployee && (
        <Modal
          isOpen={showUpdateAppointmentModal}
          onClose={() => {
            setShowUpdateAppointmentModal(false);
            setEditingAppointment(null);
            setAppointmentFormData({
              customerPhoneNumber: '',
              employeeId: user?.userId || '',
              appointmentTime: '',
              status: 'SCHEDULED',
              description: '',
            });
          }}
          title="Cập Nhật Lịch Hẹn"
          footer={
            <>
              <Button
                variant="secondary"
                onClick={() => {
                  setShowUpdateAppointmentModal(false);
                  setEditingAppointment(null);
                  setAppointmentFormData({
                    customerPhoneNumber: '',
                    employeeId: user?.userId || '',
                    appointmentTime: '',
                    status: 'SCHEDULED',
                    description: '',
                  });
                }}
              >
                Hủy
              </Button>
              <Button onClick={handleUpdateAppointment} disabled={appointmentLoading}>
                {appointmentLoading ? 'Đang cập nhật...' : 'Cập Nhật'}
              </Button>
            </>
          }
        >
          <form onSubmit={handleUpdateAppointment}>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Thời Gian Hẹn <span className="text-red-500">*</span>
              </label>
              <input
                type="datetime-local"
                value={appointmentFormData.appointmentTime}
                onChange={(e) => setAppointmentFormData({ ...appointmentFormData, appointmentTime: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Trạng Thái <span className="text-red-500">*</span>
              </label>
              <select
                value={appointmentFormData.status}
                onChange={(e) => setAppointmentFormData({ ...appointmentFormData, status: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              >
                <option value="SCHEDULED">SCHEDULED</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Mô Tả
              </label>
              <textarea
                value={appointmentFormData.description}
                onChange={(e) => setAppointmentFormData({ ...appointmentFormData, description: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                rows={3}
                placeholder="Ví dụ: Khám định kì cho khách hàng"
              />
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};

export default AppointmentPage;

