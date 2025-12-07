package com.thanhluu.tlcn.DTO.response.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResp {
  private String name;
  private String price;
  private String quantity;
}
