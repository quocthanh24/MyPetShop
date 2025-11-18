package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.response.Order.CancelOrderResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.Entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class, DiscountMapper.class})
public interface OrderMapper {
  @Mapping(source = "orderDetails", target = "orderItems")
  OrderResp toOrderResp(OrderEntity orderEntity);
  CancelOrderResp toCancelOrderResp(OrderEntity orderEntity);
}
