package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.response.Shipment.ProvinceResp;
import com.thanhluu.tlcn.Entity.ProductEntity;
import com.thanhluu.tlcn.Entity.ProvinceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {
  @Mapping(source = "provinceName", target = "provinceName")
  ProvinceEntity toEntity(ProvinceResp resp);

}
