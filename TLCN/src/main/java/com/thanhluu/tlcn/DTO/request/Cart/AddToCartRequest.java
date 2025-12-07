package com.thanhluu.tlcn.DTO.request.Cart;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddToCartRequest {
  @NotEmpty(message = "User ID should not be empty")
  private String userId;
  @NotEmpty(message = "Product ID should not be empty")
  private String productId;
  @Positive(message = "Quantity of a product shoule be greater or equal 1")
  private Integer quantity;
}
