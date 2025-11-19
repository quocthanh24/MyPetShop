package com.thanhluu.tlcn.DTO.request.Payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMomoPaymentRequest {
  @NotBlank(message = "orderId is required")
  private String orderId;

  @NotNull(message = "amount is required")
  @Positive(message = "amount must be greater than zero")
  private String amount;
}

