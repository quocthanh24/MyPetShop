package com.thanhluu.tlcn.DTO.response.Discount;

import com.thanhluu.tlcn.Enum.DiscountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiscountResp {
  private UUID id;
  private String discountName;
  private String discountCode;
  private Double percent;
  private DiscountStatus status;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Integer totalLimit;
}
