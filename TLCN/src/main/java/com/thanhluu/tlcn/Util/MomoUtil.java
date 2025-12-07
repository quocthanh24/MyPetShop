package com.thanhluu.tlcn.Util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MomoUtil {

  public static String generateSignature(String rawData, String secretKey) {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(
        secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
      );
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(secretKeySpec);
      byte[] hash = mac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();  // Lowercase hex
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException("Signature generation error: " + e.getMessage());
    }
  }
}