package com.thanhluu.tlcn.DTO.request.Shipment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddressReq {
  private String name;            // e.g., "TinTest124"
  private String phone;           // e.g., "0987654321"
  private String address;         // e.g., "72 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Vietnam"
  private String wardCode;        // e.g., "Phường 14"
  private String districtId;    // e.g., "Quận 10"
  private String provinceName;
}
