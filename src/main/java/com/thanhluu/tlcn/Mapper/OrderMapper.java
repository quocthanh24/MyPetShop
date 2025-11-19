package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.response.Order.OrderStatusResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.Entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class, DiscountMapper.class})
public interface OrderMapper {
  @Mapping(source = "id", target = "orderId")
  @Mapping(source = "orderDetails", target = "orderItems")
  OrderResp toOrderResp(OrderEntity orderEntity);
  OrderStatusResp toOrderStatusResp(OrderEntity orderEntity);
}
