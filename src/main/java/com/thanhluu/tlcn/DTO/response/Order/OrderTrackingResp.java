package com.thanhluu.tlcn.DTO.response.Order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderTrackingResp {
  private String orderNumber;
  private LocalDateTime orderDate;
  private Double totalPrice;
}
