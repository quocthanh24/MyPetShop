package com.thanhluu.tlcn.DTO.request.Shipment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.thanhluu.tlcn.DTO.request.Order.OrderItemReq;
import com.thanhluu.tlcn.Enum.GHN.RequiredNote;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShippingOrderReq {
  private Integer paymentTypeId;  // e.g., 2
  private String note;            // e.g., "Tintest 123"
  private RequiredNote requiredNote;    // e.g., "KHONGCHOXEMHANG"

  private String clientOrderCode; // e.g., ""

  // FROM
  private String fromName;
  private String fromPhone;
  private String fromAddress;
  private String fromWardCode;
  private String fromDistrictId;
  private String fromProvinceName;

  // TO
  private String toName;
  private String toPhone;
  private String toAddress;
  private String toWardCode;
  private String toDistrictId;
  private String toProvinceName;

  private Integer codAmount;      // e.g., 200000
  private String content;         // e.g., "Theo New York Times"

  @Max(value = 200) // Typical GHN limit
  private Integer length;         // e.g., 12

  @Max(value = 200)
  private Integer width;          // e.g., 12

  @Max(value = 200)
  private Integer height;         // e.g., 12

  @Max(value = 1600000) // Max weight in grams
  private Integer weight;         // e.g., 1200

//  private Integer codFailedAmount; // e.g., 2000
//  private Integer pickStationId;  // e.g., 1444
//  private Integer deliverStationId; // e.g., null
//  private Integer insuranceValue; // e.g., 10000000
  private Integer serviceTypeId;  // e.g., 2
//  private Object coupon;          // e.g., null
//  private Long pickupTime;        // e.g., 1692840132 (Unix timestamp)
//  private List<Integer> pickShift; // e.g., [2]

  private List<OrderItemReq> items;
}
