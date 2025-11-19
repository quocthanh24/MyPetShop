package com.thanhluu.tlcn.DTO.response.Order;

import com.thanhluu.tlcn.DTO.response.Discount.CreateDiscountOrderResp;
import com.thanhluu.tlcn.Enum.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResp {
  private String orderId;
  private String orderNumber;
  private String shippingAddress;
  private String phoneNumber;
  private Double totalPrice;
  private Double shippingFee;
  private String note;// optional
  private PaymentMethod paymentMethod;
  private CreateDiscountOrderResp discount; // optional
  private List<OrderItemResp> orderItems;
}
