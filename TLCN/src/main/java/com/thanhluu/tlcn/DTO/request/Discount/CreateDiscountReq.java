package com.thanhluu.tlcn.DTO.request.Discount;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDiscountReq {
  private String discountName;
  private String discountCode;
  @Positive(message = "Discount percent should be greater than 0")
  private Double percent;
  @NotNull(message = "Start Date should not be null")
  private LocalDateTime startDate;
  @NotNull(message = "End Date should not be null")
  private LocalDateTime endDate;
  private Integer totalLimit;
}
