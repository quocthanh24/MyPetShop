package com.thanhluu.tlcn.DTO.response.Discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewDiscountResp {
  private UUID id;
  private String discountCode;
  private Double percent;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Integer totalLimit;
  private Integer usedCount;
}
