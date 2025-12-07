package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.response.Shipment.DistrictResp;
import com.thanhluu.tlcn.Entity.DistrictEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DistrictMapper {
  DistrictEntity toEntity(DistrictResp districtResp);
}
