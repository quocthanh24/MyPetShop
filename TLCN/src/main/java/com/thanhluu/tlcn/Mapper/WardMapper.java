package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.response.Shipment.WardResp;
import com.thanhluu.tlcn.Entity.WardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WardMapper {
  @Mapping(target = "wardId", source = "wardCode")
  WardEntity toEntity(WardResp wardResp);
}
