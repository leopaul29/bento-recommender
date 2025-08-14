package com.leopaul29.bento.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev") // Swagger uniquement en dev
public class OpenApiConfig {

    @Bean
    public OpenAPI bentoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bento Recommender API")
                        .description("API for bentos and recommandations")
                        .version("1.0"));
    }
}
