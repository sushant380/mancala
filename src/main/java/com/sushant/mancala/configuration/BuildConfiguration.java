package com.sushant.mancala.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Dynamic Build configuration of api info for swagger. */
@Configuration
@ConfigurationProperties(prefix = "build")
public class BuildConfiguration {
  private String title;
  private String description;
  private String version;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

 }
