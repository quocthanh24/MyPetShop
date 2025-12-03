package com.thanhluu.tlcn.DTO.response.Shipment;

import lombok.Data;

@Data
public class ShippingFeeDetailResp {
  private Integer total;
  private Integer serviceFee;
  private Integer insuranceFee;
}
