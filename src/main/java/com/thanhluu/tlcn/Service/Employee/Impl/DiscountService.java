package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.Discount.CreateDiscountReq;
import com.thanhluu.tlcn.DTO.response.Discount.CreateDiscountResp;
import com.thanhluu.tlcn.DTO.response.Discount.SetDiscountStatusResp;
import com.thanhluu.tlcn.DTO.response.Discount.ViewDiscountResp;
import com.thanhluu.tlcn.Entity.DiscountEntity;
import com.thanhluu.tlcn.Enum.DiscountStatus;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.DiscountMapper;
import com.thanhluu.tlcn.Repository.DiscountRepository;
import com.thanhluu.tlcn.Service.Employee.IDiscountService;
import com.thanhluu.tlcn.Util.DiscountCodeUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@Slf4j
public class DiscountService implements IDiscountService {
  @Autowired
  private DiscountRepository discountRepository;
  @Autowired
  private DiscountMapper discountMapper;
  @Value("${DISCOUNT_CODE_LENGTH}")
  private int codeLength;

  @Override
  public CreateDiscountResp createManually(CreateDiscountReq createDiscountReq) {
    if (createDiscountReq.getDiscountCode() == null) {
      throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
    }
    DiscountEntity discountEntity = discountMapper.toEntity(createDiscountReq);
    setStatusToActive(discountEntity);
    discountRepository.save(discountEntity);
    return discountMapper.toDTO(discountEntity);
  }

  @Override
  public CreateDiscountResp createAutomatically(CreateDiscountReq createDiscountReq) {
    DiscountEntity discountEntity = discountMapper.toEntity(createDiscountReq);
    setStatusToActive(discountEntity);
    discountEntity.setDiscountCode(DiscountCodeUtil.generate(codeLength));
    discountRepository.save(discountEntity);
    return discountMapper.toDTO(discountEntity);
  }

  @Override
  public Page<ViewDiscountResp> getDiscountCodes(Pageable pageable, DiscountStatus status) {
    Page<DiscountEntity> discounts = discountRepository.findAllByStatus(status, pageable);
    return discounts.map(discountMapper::toView);
  }

  @Override
  public SetDiscountStatusResp setDiscountToInactive(String id) {
    DiscountEntity discountEntity = discountRepository.findByIdAndStatusIs(UUID.fromString(id), DiscountStatus.ACTIVE)
      .orElseThrow(() -> new BadRequestException(ErrorCode.DISCOUNT_NOT_FOUND));
    discountEntity.setStatus(DiscountStatus.INACTIVE);
    discountRepository.save(discountEntity);
    return discountMapper.toStatusDTO(discountEntity);
  }

  @Override
  public Page<SetDiscountStatusResp> setDiscountToExpired(Pageable pageable) {
    List<DiscountStatus> targetStatuses = List.of(DiscountStatus.ACTIVE, DiscountStatus.INACTIVE);
    Page<DiscountEntity> discounts = discountRepository.findAllByStatusIn(targetStatuses, pageable);
    discounts.getContent().forEach(discount -> {
      if (!discount.getEndDate().isBefore(now())) {
        log.info("Discount {} is still active, skipped.", discount.getDiscountCode());
        return;
      }
      discount.setStatus(DiscountStatus.EXPIRED);
    });
    discountRepository.saveAll(discounts.getContent());
    return discounts.map(discountMapper::toStatusDTO);
  }

  public void setStatusToActive(DiscountEntity discountEntity) {
    LocalDateTime startDate = discountEntity.getStartDate();
    LocalDateTime endDate = discountEntity.getEndDate();
    if (now().isAfter(startDate) && now().isBefore(endDate)) {
      discountEntity.setStatus(DiscountStatus.ACTIVE);
    }
    else {
      discountEntity.setStatus(DiscountStatus.INACTIVE);
    }
  }
}
