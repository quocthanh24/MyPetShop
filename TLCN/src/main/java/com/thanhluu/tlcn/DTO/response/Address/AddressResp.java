package com.thanhluu.tlcn.DTO.response.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResp {
  private String wardId;
  private String districtId;
  private String wardName;
  private String districtName;
}
