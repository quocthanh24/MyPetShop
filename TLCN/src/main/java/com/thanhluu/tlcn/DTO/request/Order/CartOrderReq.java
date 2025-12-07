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
public class CartOrderReq {
  private List<UUID> cartItemIds;
  @NotEmpty(message = "Sender's Address should not be empty")
  private String fromAddress;
  @NotEmpty(message = "Customer's Address should not be empty")
  private String toAddress;
  @NotEmpty(message = "Phone Number should not be empty")
  private String phoneNumber;
  private String note;// optional
  private PaymentMethod paymentMethod;
  private String discountCode; // optional
  // Thông tin cân nặng và kích thước hàng hóa (sẽ được tính từ cart items hoặc truyền từ client)
  private Integer weight; // gram - optional, nếu null sẽ tính từ products
    private Integer length; // cm - optional
  private Integer width; // cm - optional
  private Integer height; // cm - optional
  private Integer serviceType; // 5: Hàng nặng, 2: Hàng nhẹ - default 2
}
