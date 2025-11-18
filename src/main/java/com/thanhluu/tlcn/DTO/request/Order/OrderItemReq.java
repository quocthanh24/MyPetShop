package com.thanhluu.tlcn.DTO.request.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReq {
  private String productId;
  private Integer quantity;
}
