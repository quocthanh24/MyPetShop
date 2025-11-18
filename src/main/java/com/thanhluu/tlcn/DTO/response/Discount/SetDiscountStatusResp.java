package com.thanhluu.tlcn.DTO.response.Discount;

import com.thanhluu.tlcn.Enum.DiscountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetDiscountStatusResp {
  private UUID id;
  private DiscountStatus status;
}
