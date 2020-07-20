package com.sushant.mancala.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/** Swagger integration to support Open API specification 2. */


@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI(BuildConfiguration buildConfiguration) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title(buildConfiguration.getTitle()).description(
                        buildConfiguration.getDescription()).version(buildConfiguration.getVersion()));
    }
}

