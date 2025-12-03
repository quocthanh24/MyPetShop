package com.thanhluu.tlcn.Util;

import com.thanhluu.tlcn.Config.GhnConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GhnUtil {
  private final GhnConfig ghnConfig;

  public HttpHeaders buildHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Token", ghnConfig.getToken());
    headers.set("ShopId", ghnConfig.getShopId());
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  public String buildUrl(String path) {
    return ghnConfig.getBaseUrl() + path;
  }
}
