package com.thanhluu.tlcn.Service.Customer;


import com.thanhluu.tlcn.DTO.request.Order.CartOrderReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderCreateReq;
import com.thanhluu.tlcn.DTO.response.Order.CancelOrderResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;

public interface IOrderService {
  OrderResp cartOrder(String userId, CartOrderReq req);
  OrderResp buyNowOrder(OrderCreateReq request);
  CancelOrderResp cancelOrder(String orderNumber);
}


