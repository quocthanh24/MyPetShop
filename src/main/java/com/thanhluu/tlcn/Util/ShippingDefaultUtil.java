package com.thanhluu.tlcn.Util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shipping")
@Getter
public class ShippingDefaultUtil {

  @Value("${shipping.default_height}")
  private int defaultHeight;

  @Value("${shipping.default_weight}")
  private int defaultWeight;

  @Value("${shipping.default_length}")
  private int defaultLength;

  @Value("${shipping.default_width}")
  private int defaultWidth;

}
