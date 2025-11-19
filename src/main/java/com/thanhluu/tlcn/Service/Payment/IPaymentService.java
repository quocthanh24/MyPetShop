package com.thanhluu.tlcn.Service.Payment;

import java.util.Map;

public interface IPaymentService {
  Map<String, Object> createPayment(String orderId, String amount);
  boolean handleIpn(Map<String, Object> data);
  String momoIpn(Map<String, Object> payload);
}
