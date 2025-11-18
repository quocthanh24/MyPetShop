package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Rating.RatingReq;
import com.thanhluu.tlcn.DTO.response.Rating.RatingResp;
import com.thanhluu.tlcn.Entity.RatingEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {
  RatingEntity toEntity(RatingReq req);
  RatingResp toDto(RatingEntity entity);
}
