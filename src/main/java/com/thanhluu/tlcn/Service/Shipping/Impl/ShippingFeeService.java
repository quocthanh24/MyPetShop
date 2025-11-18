package com.thanhluu.tlcn.Service.Shipping.Impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanhluu.tlcn.Service.Employee.ICategoryService;
import com.thanhluu.tlcn.Service.Shipping.IShippingFeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingFeeService implements IShippingFeeService {

  @Value("${google.maps.api.key}")
  private String googleApiKey;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RestTemplate restTemplate = new RestTemplate();

  public double calculateShippingFee(String originAddress, String destinationAddress) {
//    double distanceKm = fetchDistanceInKm(originAddress, destinationAddress);
    double distanceKm = 6.0;
    double fee;
    if (distanceKm < 5.0) fee = 15000d;
    else if (distanceKm <= 10.0) fee = 25000d;
    else fee = 35000d;

    log.info("Distance Matrix distance: {} km -> shipping fee: {}", String.format("%.2f", distanceKm), fee);
    return fee;
  }

//  private double fetchDistanceInKm(String originAddress, String destinationAddress) {
//    try {
//      URI uri = UriComponentsBuilder
//        .fromUriString("https://maps.googleapis.com/maps/api/distancematrix/json")
//        .queryParam("origins", originAddress)
//        .queryParam("destinations", destinationAddress)
//        .queryParam("key", googleApiKey)
//        .queryParam("units", "metric")
//        .queryParam("language", "vi")
//        .build()
//        .encode()
//        .toUri();
//
//      log.info("Calling Google Distance Matrix API: {}", uri);
//      ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
//      String body = Objects.requireNonNull(response.getBody(), "Empty response from Google API");
//
//      JsonNode root = objectMapper.readTree(body);
//      String status = root.path("status").asText();
//      if (!"OK".equals(status)) {
//        String errorMessage = root.path("error_message").asText("");
//        log.error("Google Distance Matrix API error - Status: {}, Error Message: {}, Response Body: {}",
//                  status, errorMessage.isEmpty() ? "N/A" : errorMessage, body);
//
//        String detailedError = String.format("Google API error: status=%s%s",
//            status,
//            errorMessage.isEmpty() ? "" : ", error_message=" + errorMessage);
//        throw new IllegalStateException(detailedError);
//      }
//
//      JsonNode element = root.path("rows").get(0).path("elements").get(0);
//      String elementStatus = element.path("status").asText();
//      if (!"OK".equals(elementStatus)) {
//        throw new IllegalStateException("Distance element error: status=" + elementStatus);
//      }
//
//      long distanceMeters = element.path("distance").path("value").asLong();
//      double km = distanceMeters / 1000.0d;
//      log.info("Google Distance Matrix returned distance: {} meters ({} km)", distanceMeters, String.format("%.2f", km));
//      return km;
//    } catch (Exception ex) {
//      log.error("Failed to fetch distance from Google API: {}", ex.getMessage(), ex);
//      throw new IllegalStateException("Cannot calculate distance from Google API", ex);
//    }
//  }

  
}


