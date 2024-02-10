package com.Jason.DevInternHubBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI devInternHubOpenAPI() {
        return new OpenAPI()
            .info(new Info()
            .title("DevInternHub REST API")
            .description("Internship database")
            .version("1.0"));
    }
}