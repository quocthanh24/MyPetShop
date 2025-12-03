package com.thanhluu.tlcn.Component;

import com.thanhluu.tlcn.DTO.request.Shipment.WardReq;
import com.thanhluu.tlcn.Util.GhnUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class GhnClient {
  private final GhnUtil ghnUtil;
  private final RestTemplate restTemplate = new RestTemplate();

  public <T> T callGet(String path, Class<T> responseType) {
    log.info("Calling GET {}", path);
    String url = ghnUtil.buildUrl(path);
    HttpEntity<?> entity = new HttpEntity<>(ghnUtil.buildHeaders());

    try {
      ResponseEntity<T> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, responseType);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }

      log.error("GHN error: {} - {}", response.getStatusCode(), response.getBody());
      throw new RuntimeException("GHN error: " + response.getStatusCode());

    } catch (HttpClientErrorException e) {
      log.error("GHN Client Error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected GHN error", e);
      throw new RuntimeException("Unexpected GHN error: " + e.getMessage());
    }
  }

  public <T> T callPost(String path,Object requestBody, Class<T> responseType) {
    log.info("Calling Post {}", path);
    String url = ghnUtil.buildUrl(path);
    HttpEntity<?> entity = new HttpEntity<>(requestBody,ghnUtil.buildHeaders());

    try {
      ResponseEntity<T> response =
        restTemplate.exchange(url, HttpMethod.POST,entity, responseType);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }

      log.error("GHN error: {} - {}", response.getStatusCode(), response.getBody());
      throw new RuntimeException("GHN error: " + response.getStatusCode());

    } catch (HttpClientErrorException e) {
      log.error("GHN Client Error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected GHN error", e);
      throw new RuntimeException("Unexpected GHN error: " + e.getMessage());
    }
  }
}
