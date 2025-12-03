package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Order.OrderItemReq;
import com.thanhluu.tlcn.DTO.response.Order.OrderStatusResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderTrackingResp;
import com.thanhluu.tlcn.Entity.OrderDetailEntity;
import com.thanhluu.tlcn.Entity.OrderEntity;
import com.thanhluu.tlcn.Entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class, DiscountMapper.class, ProductEntity.class})
public interface OrderMapper {
  @Mapping(source = "id", target = "orderId")
  @Mapping(source = "orderDetails", target = "orderItems")
  OrderResp toOrderResp(OrderEntity orderEntity);
  OrderStatusResp toOrderStatusResp(OrderEntity orderEntity);
  OrderTrackingResp toOrderTrackingResp(OrderEntity orderEntity);
  @Mapping(source = "product.name", target = "name")
  OrderItemReq toOrderItemReq(OrderDetailEntity orderDetail);


}
