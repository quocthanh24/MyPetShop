package com.thanhluu.tlcn.Controller.Admin;

import com.thanhluu.tlcn.Service.Shipment.Address.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AddressController {

  private final IAddressService addressService;

  @PostMapping("/provinces")
  public ResponseEntity<?> insertProvinces() {
    addressService.insertProvince();
    return new ResponseEntity<>("Successfully insert provinces", HttpStatus.OK);
  }

  @PostMapping("/districts")
  public ResponseEntity<?> insertDistricts() {
    addressService.insertDistrict();
    return new ResponseEntity<>("Successfully insert districts", HttpStatus.OK);
  }

  @PostMapping("/wards")
  public ResponseEntity<?> insertWards() {
    addressService.insertWard();
    return new ResponseEntity<>("Successfully insert wards", HttpStatus.OK);
  }
}
