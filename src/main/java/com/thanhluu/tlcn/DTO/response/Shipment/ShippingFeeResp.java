package com.thanhluu.tlcn.DTO.response.Shipment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

@Data
public class ShippingFeeResp {
  private Integer code;     // Optional, để check success (200)
  private String message;   // Optional
  private ShippingFeeDetailResp data;        // Wrapper cho data
}
