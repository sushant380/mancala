package com.sushant.mancala.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger integration to support Open API specification 2. */
@Configuration
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class SwaggerConfiguration {
  @Bean
  public OpenAPI customOpenAPI(BuildConfiguration buildConfiguration) {
    return new OpenAPI()
        .components(new Components())
        .externalDocs(
            new ExternalDocumentation()
                .description("Mancala WIKI")
                .url(buildConfiguration.getWiki()))
        .info(
            new Info()
                .title(buildConfiguration.getTitle())
                .description(buildConfiguration.getDescription())
                .version(buildConfiguration.getVersion())
                .contact(new Contact().email(buildConfiguration.getContactEmail()))
                .license(
                    new License()
                        .name(buildConfiguration.getLicense())
                        .url(buildConfiguration.getLicenseLink())));
  }
}
