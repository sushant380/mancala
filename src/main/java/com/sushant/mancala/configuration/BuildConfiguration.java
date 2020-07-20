package com.sushant.mancala.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Dynamic Build configuration of api info for swagger. */
@Configuration
@ConfigurationProperties(prefix = "build")
@Data
public class BuildConfiguration {
  private String title;
  private String description;
  private String version;
  private String license;
  private String licenseLink;
  private String contactEmail;
  private String wiki;
}
