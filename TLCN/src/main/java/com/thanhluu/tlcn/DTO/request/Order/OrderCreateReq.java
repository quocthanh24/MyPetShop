package com.thanhluu.tlcn.DTO.request.Order;


import com.thanhluu.tlcn.Enum.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateReq {
  @NotEmpty(message = "User ID should not be empty")
  private String userId;
  @NotEmpty(message = "Shipping Address should not be empty")
  private String shippingAddress;
  @NotEmpty(message = "Phone Number should not be empty")
  private String phoneNumber;
  @NotEmpty(message = "Order must contain at least one product")
  private List<OrderItemReq> orderItems;
  private String note;// optional
  private PaymentMethod paymentMethod;
  private String discountCode; // optional
}


