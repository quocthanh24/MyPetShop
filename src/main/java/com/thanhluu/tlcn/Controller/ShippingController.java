package com.thanhluu.tlcn.Controller;

import com.thanhluu.tlcn.DTO.request.Shipment.WardReq;
import com.thanhluu.tlcn.DTO.response.Shipment.ViewDistrictsResp;
import com.thanhluu.tlcn.DTO.response.Shipment.ViewProvincesResp;
import com.thanhluu.tlcn.DTO.response.Shipment.ViewWardsResp;
import com.thanhluu.tlcn.Service.Shipment.IShipmentService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipment")
@RequiredArgsConstructor
public class ShippingController {

  private final IShipmentService shipmentService;

  @GetMapping("/provinces")
  public ViewProvincesResp getProvinces() {
    return shipmentService.viewProvinces();
  }

  @GetMapping("/districts")
  public ViewDistrictsResp getDistricts(@RequestParam @NotNull String provinceId) {
    return shipmentService.viewDistricts(provinceId);
  }

  @GetMapping("/wards")
  public ViewWardsResp getWards(@RequestParam @NotNull String districtId) {
    return shipmentService.viewWards(districtId);
  }
}
