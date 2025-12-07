package com.thanhluu.tlcn.Util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class DiscountCodeUtil {
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom random = new SecureRandom();

  public static String generate(int length) {
    StringBuilder code = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(CHARACTERS.length());
      code.append(CHARACTERS.charAt(index));
    }
    return code.toString();
  }
}
