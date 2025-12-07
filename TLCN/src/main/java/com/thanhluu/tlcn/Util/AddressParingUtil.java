package com.thanhluu.tlcn.Util;

import com.thanhluu.tlcn.DTO.request.Shipment.AddressReq;
import com.thanhluu.tlcn.DTO.response.Address.AddressResp;
import com.thanhluu.tlcn.Entity.DistrictEntity;
import com.thanhluu.tlcn.Entity.WardEntity;
import com.thanhluu.tlcn.Repository.DistrictRepository;
import com.thanhluu.tlcn.Repository.WardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddressParingUtil {

  private final WardRepository wardRepository;
  private final DistrictRepository districtRepository;

  public AddressResp parseAddress(String address) {
    String[] addressParts = address.split(",");
    log.info("Địa chỉ : {}", addressParts[0] );
    log.info("Xã / Phường : {}", addressParts[1] );
    log.info("Huyện / Thành Phố : {}", addressParts[2] );
    log.info("Tỉnh : {}", addressParts[3] );
    String districtName = addressParts[2];
    String districtId = findDistrictIdByName(districtName);
    String wardName = addressParts[1];
    String wardId = findWardIdByName(wardName);

    return AddressResp.builder()
      .districtName(districtName)
      .districtId(districtId)
      .wardId(wardId)
      .wardName(wardName)
      .build();

  }

  public String getWardCodeFromAddress(String address) {
    String[] addressParts = address.split(",");
    String wardName = addressParts[1];
    return findWardIdByName(wardName);
  }

  public String getDistrictIdFromAddress(String address) {
    String[] addressParts = address.split(",");
    String districtName = addressParts[2];
    return findDistrictIdByName(districtName);
  }



  public String getProvinceNameFromAddress(String address) {
    String[] addressParts = address.split(",");
    log.info("Length of address in province: {}", addressParts.length);
    return addressParts[3];
  }

  private String findDistrictIdByName(String districtName) {
    List<DistrictEntity> districts = districtRepository.findDistinctByName(districtName);
    return districts.get(0).getDistrictId();
  }

  private String findWardIdByName(String wardName) {
    List<WardEntity> wards = wardRepository.findWardByName(wardName);
    return wards.get(0).getWardId();
  }
}
