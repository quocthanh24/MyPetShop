package com.thanhluu.tlcn.Service.Shipment.Impl;

import com.thanhluu.tlcn.Component.GhnClient;
import com.thanhluu.tlcn.DTO.request.Shipment.DistrictReq;
import com.thanhluu.tlcn.DTO.request.Shipment.ShippingFeeReq;
import com.thanhluu.tlcn.DTO.request.Shipment.ShippingOrderReq;
import com.thanhluu.tlcn.DTO.request.Shipment.WardReq;
import com.thanhluu.tlcn.DTO.response.Shipment.*;
import com.thanhluu.tlcn.Service.Shipment.IShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService implements IShipmentService {

  private final GhnClient ghnClient;

  @Override
  public ViewProvincesResp viewProvinces() {
    return ghnClient.callGet("/master-data/province", ViewProvincesResp.class);
  }

  @Override
  public ViewWardsResp viewWards(String districtId) {
    WardReq body = new WardReq(Integer.parseInt(districtId));
    return ghnClient.callPost("/master-data/ward", body, ViewWardsResp.class);
  }

  @Override
  public ViewDistrictsResp viewDistricts(String provinceId) {
    DistrictReq body = new DistrictReq(Integer.parseInt(provinceId));
    return ghnClient.callPost("/master-data/district", body, ViewDistrictsResp.class);
  }

  @Override
  public Integer calculateFee(ShippingFeeReq body) {
    log.info("Calculating fee for shipping fee: {}", body);
    ShippingFeeResp resp = ghnClient.callPost("/v2/shipping-order/fee", body, ShippingFeeResp.class);

    if (resp.getCode() != 200 || resp.getData() == null) {
      throw new RuntimeException("GHN API error: " + resp.getMessage());
    }

    return resp.getData().getTotal();
  }

  @Override
  public String createShippingOrder(ShippingOrderReq body) {
    log.info("Creating shipping order: {}", body.toString());
    ShippingOrderResp resp = ghnClient.callPost("/v2/shipping-order/create", body, ShippingOrderResp.class);

    log.info("Shipping Order Created: {}", resp.toString());
    if (resp.getCode() != 200 || resp.getData() == null) {
      throw new RuntimeException("GHN API error: " + resp.getMessage());
    }

    return resp.getData().getOrderCode();
  }

}
