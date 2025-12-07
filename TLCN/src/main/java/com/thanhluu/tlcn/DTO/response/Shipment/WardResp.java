package com.thanhluu.tlcn.DTO.response.Shipment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WardResp {
  @JsonProperty("WardCode")
  private String wardCode;
  @JsonProperty("DistrictID")
  private String districtId;
  @JsonProperty("WardName")
  private String wardName;
  @JsonProperty("NameExtension")
  private List<String> nameExtension;
}
