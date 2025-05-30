package com.becareful.becarefulserver.global.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Server dev_server = new Server();
        dev_server.setUrl("https://blaybus.everdu.com");

        Server local_server = new Server();
        local_server.setUrl("http://localhost:8080");

        return new OpenAPI()
                .servers(List.of(dev_server, local_server))
                .addSecurityItem(securityRequirement())
                .components(authSetting());
    }

    private Components authSetting() {
        return new Components()
                .addSecuritySchemes(
                        "Authorization",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name(AUTHORIZATION));
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(AUTHORIZATION);
    }
}
