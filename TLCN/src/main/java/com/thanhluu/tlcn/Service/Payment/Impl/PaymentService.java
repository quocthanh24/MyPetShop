package com.thanhluu.tlcn.Service.Payment.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanhluu.tlcn.DTO.request.Payment.MOMOReq;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Enum.OrderStatus;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Service.Customer.IOrderService;
import com.thanhluu.tlcn.Service.Payment.IPaymentService;
import com.thanhluu.tlcn.Util.MomoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;  // Sort alpha
import java.util.UUID;

@Service
@Slf4j
public class PaymentService implements IPaymentService {
  @Value("${momo.endpoint}")
  private String endpoint;

  @Value("${momo.partnerCode}")
  private String partnerCode;

  @Value("${momo.accessKey}")
  private String accessKey;

  @Value("${momo.secretKey}")
  private String secretKey;

  // URL để redirect người dùng
  @Value("${momo.redirectUrl}")
  private String redirectUrl;

  // URL để MoMo gọi IPN (Server nhận trạng thái thanh toán)
  @Value("${momo.ipnUrl}")
  private String ipnUrl;

  @Autowired
  private IOrderService orderService;

  @Override
  public Map<String, Object> createPayment(String orderId, String amount) {
    try {
      String requestId = UUID.randomUUID().toString();
      String orderInfo = "pay with MoMo";  // Hoặc "Thanh toán đơn hàng #" + orderId (UTF-8 OK)
      String extraData = "";

      // Build params với TreeMap (sort alpha tự động)
      Map<String, String> params = new TreeMap<>();
      params.put("accessKey", accessKey);
      params.put("amount", String.valueOf(amount));
      params.put("extraData", extraData);
      params.put("ipnUrl", ipnUrl);
      params.put("orderId", orderId);
      params.put("orderInfo", orderInfo);
      params.put("partnerCode", partnerCode);
      params.put("redirectUrl", redirectUrl);
      params.put("requestId", requestId);
      params.put("requestType", "captureWallet");


      // Build rawSignature (key=value& no encode values)
      StringBuilder rawSignatureBuilder = new StringBuilder();
      for (Map.Entry<String, String> entry : params.entrySet()) {
        if (!rawSignatureBuilder.isEmpty()) {
          rawSignatureBuilder.append("&");
        }
        rawSignatureBuilder.append(entry.getKey()).append("=").append(entry.getValue());
      }
      String rawSignature = rawSignatureBuilder.toString();

      log.info("MoMo rawSignature (sorted): {}", rawSignature);  // So sánh với error raw

      String signature = MomoUtil.generateSignature(rawSignature, secretKey);
      log.info("MoMo signature: {}", signature);

      // Build req với tên chuẩn
      MOMOReq req = MOMOReq.builder()
        .partnerCode(partnerCode)
        .partnerName("Test")
        .storeId("MomoTestStore")
        .accessKey(accessKey)
        .requestId(requestId)
        .amount(amount)
        .orderId(orderId)
        .orderInfo(orderInfo)
        .redirectUrl(redirectUrl)
        .ipnUrl(ipnUrl)
        .lang("en")
        .extraData(extraData)
        .requestType("captureWallet")
        .signature(signature)
        .build();

      // ====== Send request ======
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<MOMOReq> entity = new HttpEntity<>(req, headers);
      log.info("MoMo req: {}", entity.toString());
      ResponseEntity<Map> response = restTemplate.postForEntity(
        endpoint,
        entity,
        Map.class
      );

      log.info("MoMo response = {}", response.getBody());

      return response.getBody();

    } catch (Exception e) {
      log.error("MoMo create payment error: {}", e.getMessage(), e);
      throw new RuntimeException("Cannot create MoMo payment");
    }
  }

  // Update handleIpn tương tự: dùng TreeMap cho IPN raw (params khác)
  @Override
  public boolean handleIpn(Map<String, Object> data) {
    try {
      if (CollectionUtils.isEmpty(data)) {
        log.warn("Empty MoMo IPN payload");
        return false;
      }

      // Lấy signature MoMo gửi
      String receivedSignature = mapValueAsString(data, "signature");
      if (!StringUtils.hasText(receivedSignature)) {
        log.warn("Missing signature in MoMo IPN payload: {}", data);
        return false;
      }

      Map<String, String> fields = new TreeMap<>(); // TreeMap tự sắp alphabet
      fields.put("accessKey", accessKey); // Từ config
      fields.put("amount", mapValueAsString(data, "amount"));
      fields.put("extraData", mapValueAsString(data, "extraData"));
      fields.put("message", mapValueAsString(data, "message"));
      fields.put("orderId", mapValueAsString(data, "orderId"));
      fields.put("orderInfo", mapValueAsString(data, "orderInfo"));
      fields.put("orderType", mapValueAsString(data, "orderType"));
      fields.put("partnerCode", mapValueAsString(data, "partnerCode"));
      fields.put("payType", mapValueAsString(data, "payType"));
      fields.put("requestId", mapValueAsString(data, "requestId"));
      fields.put("resultCode", mapValueAsString(data, "resultCode"));
      fields.put("responseTime", mapValueAsString(data, "responseTime"));
      fields.put("transId", mapValueAsString(data, "transId"));

      // Ghép raw: key1=value1&key2=value2...
      StringBuilder rawBuilder = new StringBuilder();
      for (Map.Entry<String, String> entry : fields.entrySet()) {
        if (!rawBuilder.isEmpty()) {
          rawBuilder.append("&");
        }
        rawBuilder.append(entry.getKey()).append("=").append(entry.getValue());
      }
      String rawIpn = rawBuilder.toString();

      String expectedSignature = MomoUtil.generateSignature(rawIpn, secretKey);

      log.info("receivedSignature: {}", receivedSignature);
      log.info("expectedSignature: {}", expectedSignature);
      log.info("IPN raw string: {}", rawIpn);

      boolean verified = expectedSignature.equals(receivedSignature);
      if (!verified) {
        log.warn("MoMo IPN signature mismatch! Payload: {}", data);
      }

      return verified;

    } catch (Exception e) {
      log.error("Handle IPN error: {}", e.getMessage(), e);
      return false;
    }
  }

  @Override
  public String momoIpn(Map<String, Object> payload) {
    if (CollectionUtils.isEmpty(payload)) {
      return "EMPTY PAYLOAD";
    }

    boolean verified = handleIpn(payload);

    if (!verified) {
      throw new BadRequestException(ErrorCode.INVALID_SIGNATURE);
    }

    Integer resultCode = extractResultCode(payload)
      .orElse(null);

    if (resultCode == null) {
      log.warn("Missing resultCode in MoMo IPN payload: {}", payload);
      throw new BadRequestException(ErrorCode.MISSING_RESULT_CODE);
    }

    // Lưu orderNumber thay vì orderId
    String orderNumber = payload.get("orderId").toString();

    if (resultCode == 0) {
      // TODO: update order status = PAID
      orderService.updateOrderStatus(orderNumber, OrderStatus.PAID);
      return "Order Status Updated to PAID";
    } else {
      // TODO: update status = FAILED
      orderService.updateOrderStatus(orderNumber, OrderStatus.PAYMENT_FAILED);
      return "Order Status Updated to PAYMENT_FAILED";
    }
  }

  private String mapValueAsString(Map<String, Object> data, String key) {
    Object value = data.get(key);
    return value == null ? "" : value.toString();
  }

  private Optional<Integer> extractResultCode(Map<String, Object> payload) {
    Object value = payload.get("resultCode");
    if (value == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(Integer.parseInt(value.toString()));
    } catch (NumberFormatException ex) {
      log.warn("Invalid resultCode format in MoMo IPN payload: {}", payload, ex);
      return Optional.empty();
    }
  }

}