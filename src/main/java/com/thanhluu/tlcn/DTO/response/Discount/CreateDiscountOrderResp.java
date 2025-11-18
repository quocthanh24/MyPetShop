package com.thanhluu.tlcn.DTO.response.Discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDiscountOrderResp {
  private String discountCode;
  private String discountName;
  private Double percent;
}
