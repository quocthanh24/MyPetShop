package com.thanhluu.tlcn.DTO.response.Shipment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProvinceResp {
  @JsonProperty("ProvinceID")
  private Integer provinceId;

  @JsonProperty("ProvinceName")
  private String provinceName;

  @JsonProperty("Code")
  private String code;

  @JsonProperty("NameExtension")
  private List<String> nameExtension;
}
