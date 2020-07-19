package com.sushant.mancala;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class MancalaApplication {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
            .components(new Components())
            .info(new Info().title("Contact Application API").description(
                    "This is a sample Spring Boot RESTful service using springdoc-openapi and OpenAPI 3."));
  }
  public static void main(String[] args) {
    SpringApplication.run(MancalaApplication.class, args);
  }
}
