package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Discount.CreateDiscountReq;
import com.thanhluu.tlcn.DTO.response.Discount.CreateDiscountOrderResp;
import com.thanhluu.tlcn.DTO.response.Discount.CreateDiscountResp;
import com.thanhluu.tlcn.DTO.response.Discount.SetDiscountStatusResp;
import com.thanhluu.tlcn.DTO.response.Discount.ViewDiscountResp;
import com.thanhluu.tlcn.Entity.DiscountEntity;
import com.thanhluu.tlcn.Entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
  CreateDiscountResp toDTO(DiscountEntity discountEntity);
  ViewDiscountResp toView(DiscountEntity discountEntity);
  DiscountEntity toEntity(CreateDiscountReq createDiscountReq);
  CreateDiscountOrderResp toCreateDiscountOrderResp(DiscountEntity discountEntity);
  SetDiscountStatusResp toStatusDTO(DiscountEntity discountEntity);
}
