package com.thanhluu.tlcn.DTO.response.Shipment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShippingOrderResp {
  private Integer code;             // e.g., 200
  private String message;           // e.g., "Success"
  private ShippingOrderDataResp data;                // Wrapper for order details
}
