package com.fixsy.imagenes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fixsy - API de Gestión de Imágenes")
                        .description("Microservicio para gestión de imágenes de la aplicación Fixsy. " +
                                "Permite subir, almacenar y recuperar imágenes asociadas a usuarios, vehículos y solicitudes de servicio.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Fixsy")
                                .email("soporte@fixsy.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8084")
                                .description("Servidor de desarrollo")
                ));
    }
}

