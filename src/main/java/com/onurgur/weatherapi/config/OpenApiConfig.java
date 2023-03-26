package com.onurgur.weatherapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI getOpenApiConfig() {
        return new OpenAPI();
    }
}
