package com.thanhluu.tlcn.DTO.request.Shipment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.thanhluu.tlcn.DTO.request.Order.OrderItemReq;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingFeeReq {
  private Integer serviceTypeId;  // Đổi tên field thành serviceTypeId để rõ (JSON: "service_type_id")
  private Integer fromDistrictId; // JSON: "from_district_id"
  private String fromWardCode;    // Đổi tên thành fromWardCode (JSON: "from_ward_code")
  private Integer toDistrictId;   // JSON: "to_district_id"
  private String toWardCode;      // Đổi tên thành toWardCode (JSON: "to_ward_code")

  @Max(value = 200) // Max: 200 cm
  private Integer length;

  @Max(value = 200) // Max: 200 cm
  private Integer width;

  @Max(value = 200) // Max: 200 cm
  private Integer height;

  @Max(value = 1600000) // Max: 1.600.000 gram
  private Integer weight;

  private Integer insuranceValue = 0;  // Thêm, mặc định 0 (JSON: "insurance_value")
  private Object coupon = null;         // Thêm, null (JSON: "coupon")

  private List<OrderItemReq> items;
}
