package com.thanhluu.tlcn.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ghn")
public class GhnConfig {
  private String token;
  private String shopId;
  private String clientId;
  private String baseUrl;
}
