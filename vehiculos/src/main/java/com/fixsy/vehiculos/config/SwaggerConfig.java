package com.fixsy.vehiculos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fixsy - Vehicle Service API")
                        .version("1.0.0")
                        .description("API para gestión de vehículos del sistema Fixsy")
                        .contact(new Contact()
                                .name("Fixsy Team")
                                .email("support@fixsy.com")));
    }
}

