package com.thanhluu.tlcn.DTO.request.Payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MOMOReq {
  private String partnerCode;
  private String partnerName;
  private String storeId;
  private String accessKey;
  private String requestId;
  private String amount;
  private String orderId;
  private String orderInfo;
  private String redirectUrl;  // Đổi từ redirectUrl
  private String ipnUrl;  // Đổi từ ipnUrl
  private String lang;
  private String requestType;
  private String extraData;
  private String signature;
}