package com.thanhluu.tlcn.Service.Customer;


import com.thanhluu.tlcn.DTO.request.Order.CartOrderReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderCreateReq;
import com.thanhluu.tlcn.DTO.request.Shipment.ShipmentRequestFromOwner;
import com.thanhluu.tlcn.DTO.response.Order.OrderStatusResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderTrackingResp;
import com.thanhluu.tlcn.Enum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
  OrderResp cartOrder(String userId, CartOrderReq req);
  OrderResp buyNowOrder(OrderCreateReq request);
  OrderStatusResp updateOrderStatus(String orderNumber, OrderStatus orderStatus);
  Page<OrderStatusResp> getAllOrdersArePaidAndPending(List<OrderStatus> orderStatuses, Pageable pageable);
  Page<OrderTrackingResp> getAllOrderByUser(String userId, Pageable pageable);
  String createShippingOrderByOrderNumber(ShipmentRequestFromOwner request);
}


