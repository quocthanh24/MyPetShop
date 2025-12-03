package com.thanhluu.tlcn.DTO.response.Shipment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShippingOrderDataResp {
  private String orderCode;                // e.g., "LKVBR7"
  private String sortCode;                 // e.g., "000-M-00-00"
  private String transType;                // e.g., "truck"
  private String wardEncode;               // e.g., ""
  private String districtEncode;            // Nested fee object
  private Integer totalFee;                // e.g., 22000
  private ZonedDateTime expectedDeliveryTime; // e.g., "2025-11-28T16:59:59Z"
  private String operationPartner;
}
