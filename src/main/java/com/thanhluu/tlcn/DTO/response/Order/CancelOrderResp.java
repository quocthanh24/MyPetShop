package com.thanhluu.tlcn.DTO.response.Order;

import com.thanhluu.tlcn.Enum.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderResp {
  private String orderNumber;
  private OrderStatus orderStatus;
}
