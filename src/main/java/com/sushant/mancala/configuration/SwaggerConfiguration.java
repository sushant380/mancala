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
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic",
    description =
        "Login with below credentials to use API.<br> " +
                "<b>Player 1</b>: <i>username</i>: player1&nbsp;&nbsp;&nbsp;<i>password</i>: player1<br>" +
                "<b>Player 2</b>: <i>username</i>: player2&nbsp;&nbsp;&nbsp;<i>password</i>: player2 <br>" +
                "<b>Admin to delete</b>: <i>username</i>: admin&nbsp;&nbsp;&nbsp;<i>password</i>: admin")
public class SwaggerConfiguration {
  @Bean
  public OpenAPI openAPI(BuildConfiguration buildConfiguration) {
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
