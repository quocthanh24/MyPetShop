package com.thanhluu.tlcn.Service.Shipping;

public interface IShippingFeeService {
  double calculateShippingFee(String originAddress, String destinationAddress);
}
