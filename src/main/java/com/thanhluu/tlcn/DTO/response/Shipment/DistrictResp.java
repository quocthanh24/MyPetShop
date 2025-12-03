package com.thanhluu.tlcn.DTO.response.Shipment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DistrictResp {
  @JsonProperty("DistrictID")
  private String districtId;
  @JsonProperty("ProvinceID")
  private String provinceId;
  @JsonProperty("DistrictName")
  private String districtName;
  @JsonProperty("NameExtension")
  private List<String> nameExtension;
}
