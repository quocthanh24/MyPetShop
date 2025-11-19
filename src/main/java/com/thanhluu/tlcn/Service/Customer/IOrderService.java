package com.thanhluu.tlcn.Service.Customer;


import com.thanhluu.tlcn.DTO.request.Order.CartOrderReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderCreateReq;
import com.thanhluu.tlcn.DTO.response.Order.OrderStatusResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.Enum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
  OrderResp cartOrder(String userId, CartOrderReq req);
  OrderResp buyNowOrder(OrderCreateReq request);
  OrderStatusResp updateOrderStatus(String orderNumber, OrderStatus orderStatus);
  Page<OrderStatusResp> getAllOrdersArePaid(OrderStatus orderStatus, Pageable pageable);
  Page<OrderStatusResp> getAllOrderByUser(String userId, Pageable pageable);
}


