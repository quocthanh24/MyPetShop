package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.Discount.CreateDiscountReq;
import com.thanhluu.tlcn.DTO.response.Discount.CreateDiscountResp;
import com.thanhluu.tlcn.DTO.response.Discount.SetDiscountStatusResp;
import com.thanhluu.tlcn.DTO.response.Discount.ViewDiscountResp;
import com.thanhluu.tlcn.Entity.DiscountEntity;
import com.thanhluu.tlcn.Enum.DiscountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDiscountService {
  CreateDiscountResp createManually(CreateDiscountReq createDiscountReq);
  CreateDiscountResp createAutomatically(CreateDiscountReq createDiscountReq);
  Page<ViewDiscountResp> getDiscountCodes(Pageable pageable, DiscountStatus status);
  SetDiscountStatusResp setDiscountToInactive(String id);
  Page<SetDiscountStatusResp> setDiscountToExpired(Pageable pageable);
}
