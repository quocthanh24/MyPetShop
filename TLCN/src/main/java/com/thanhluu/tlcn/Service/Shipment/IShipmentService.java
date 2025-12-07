package com.thanhluu.tlcn.Service.Shipment;

import com.thanhluu.tlcn.DTO.request.Shipment.ShippingFeeReq;
import com.thanhluu.tlcn.DTO.request.Shipment.ShippingOrderReq;
import com.thanhluu.tlcn.DTO.response.Shipment.ShippingFeeResp;
import com.thanhluu.tlcn.DTO.response.Shipment.ViewDistrictsResp;
import com.thanhluu.tlcn.DTO.response.Shipment.ViewProvincesResp;
import com.thanhluu.tlcn.DTO.response.Shipment.ViewWardsResp;

public interface IShipmentService {
  ViewProvincesResp viewProvinces();
  ViewWardsResp viewWards(String districtId);
  ViewDistrictsResp viewDistricts(String provinceId);
  Integer calculateFee(ShippingFeeReq request);
  String createShippingOrder(ShippingOrderReq request);
}
