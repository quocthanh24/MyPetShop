package com.thanhluu.tlcn.Service.Shipment.Address.Impl;

import com.thanhluu.tlcn.Component.GhnClient;
import com.thanhluu.tlcn.Config.GhnConfig;
import com.thanhluu.tlcn.DTO.request.Shipment.DistrictReq;
import com.thanhluu.tlcn.DTO.request.Shipment.WardReq;
import com.thanhluu.tlcn.DTO.response.Shipment.*;
import com.thanhluu.tlcn.Entity.DistrictEntity;
import com.thanhluu.tlcn.Entity.ProvinceEntity;
import com.thanhluu.tlcn.Entity.WardEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.DistrictMapper;
import com.thanhluu.tlcn.Mapper.ProvinceMapper;
import com.thanhluu.tlcn.Mapper.WardMapper;
import com.thanhluu.tlcn.Repository.DistrictRepository;
import com.thanhluu.tlcn.Repository.ProvinceRepository;
import com.thanhluu.tlcn.Repository.WardRepository;
import com.thanhluu.tlcn.Service.Shipment.Address.IAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService implements IAddressService {

  private final GhnClient ghnClient;
  private final ProvinceMapper provinceMapper;
  private final DistrictMapper districtMapper;
  private final WardMapper wardMapper;
  private final ProvinceRepository provinceRepository;
  private final DistrictRepository districtRepository;
  private final WardRepository wardRepository;

  @Override
  @Transactional
  public void insertProvince() {
    if (provinceRepository.count() > 0) {
      log.info("Provinces already exist. Skip import.");
      throw new BadRequestException(ErrorCode.PROVINCES_EXISTED);
    }

    ViewProvincesResp viewDTO = ghnClient.callGet("/master-data/province", ViewProvincesResp.class);
    List<ProvinceResp> ls = viewDTO.getData();
    ls.forEach(province -> {
      log.info("Province name: {}", province.getProvinceName());
    });
    List<ProvinceEntity> provinces = ls.stream()
      .map(provinceMapper::toEntity)
      .toList();

    provinceRepository.saveAll(provinces);
  }

  @Override
  @Transactional
  public void insertDistrict() {
    if (districtRepository.count() > 0) {
      log.info("District already exist. Skip import.");
      throw new BadRequestException(ErrorCode.DISTRICTS_EXISTED);
    }

    List<ProvinceEntity> provinces = provinceRepository.findAll();

    for (ProvinceEntity province : provinces) {


      DistrictReq body = new DistrictReq(Integer.parseInt(province.getProvinceId()));
      ViewDistrictsResp viewDTO = ghnClient.callPost("/master-data/district", body, ViewDistrictsResp.class);

      List<DistrictResp> ls = viewDTO.getData();
      if (ls == null || ls.isEmpty()) {
        continue;
      }

      List<DistrictEntity> districts = ls.stream()
        .map(districtMapper::toEntity)
        .toList();

      for (DistrictEntity district : districts) {
        district.setProvince(province);
      }

      districtRepository.saveAll(districts);
    }
  }

  @Override
  public void insertWard() {
    if (wardRepository.count() > 0) {
      log.info("Ward already exist. Skip import.");
      throw new BadRequestException(ErrorCode.WARDS_EXISTED);
    }

    List<DistrictEntity> districts = districtRepository.findAll();

    for (DistrictEntity district : districts) {


      WardReq body = new WardReq(Integer.parseInt(district.getDistrictId()));
      ViewWardsResp viewDTO = ghnClient.callPost("/master-data/ward", body, ViewWardsResp.class);

      List<WardResp> ls = viewDTO.getData();
      if (ls == null || ls.isEmpty()) {
        continue;
      }

      List<WardEntity> wards = ls.stream()
        .map(wardMapper::toEntity)
        .toList();

      for (WardEntity ward : wards) {
        ward.setDistrict(district);
      }
      wardRepository.saveAll(wards);
    }
  }
}
