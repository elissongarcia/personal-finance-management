package com.tenantmanagement.config;

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
                        .title("Tenant Management API")
                        .description("A comprehensive API for managing tenants in a multi-tenant application using Axon Framework and Event Sourcing")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tenant Management Team")
                                .email("admin@tenantmanagement.com")
                                .url("https://tenantmanagement.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.tenantmanagement.com")
                                .description("Production server")
                ));
    }
} 