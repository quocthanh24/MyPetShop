package com.thanhluu.tlcn.DTO.response.Order;

import com.thanhluu.tlcn.Enum.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResp {
  private String orderNumber;
  private OrderStatus orderStatus;
}
