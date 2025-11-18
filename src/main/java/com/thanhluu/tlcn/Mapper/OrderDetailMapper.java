package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Order.OrderItemReq;
import com.thanhluu.tlcn.DTO.response.Order.OrderItemResp;
import com.thanhluu.tlcn.Entity.OrderDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
  @Mapping(source = "product.name", target = "name")
  @Mapping(source = "product.price", target = "price")
  @Mapping(source = "quantity", target = "quantity")
  OrderItemResp toOrderItemResp(OrderDetailEntity orderDetail);
}
