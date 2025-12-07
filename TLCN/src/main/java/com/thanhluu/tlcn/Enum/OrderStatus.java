package com.thanhluu.tlcn.Enum;

public enum OrderStatus {
  PENDING, // Chờ xác nhận
  CONFIRMED,  // Đã xác nhận
  PROCESSING, // Đang chuẩn bị hàng
  SHIPPING, // Đang giao hàng
  OUT_FOR_DELIVERY, // Shipper đang giao
  PAID, // Đã thanh toán
  PAYMENT_FAILED, // Thanh toán thất bại
  DELIVERED,  // Đã giao hàng
  CANCELED  // Đã hủy đơn hàng
}
