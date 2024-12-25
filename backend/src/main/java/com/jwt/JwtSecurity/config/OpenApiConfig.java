package com.jwt.JwtSecurity.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Rishi's Application",
                        email = "adeeparishi@gmail.com",
                        url = "www.google.com"
                ),
                description = "Documentation for my JWT Application",
                title = "Rishi's Application",
                version = "1.0",
                license = @License(
                        name = "Rishi Private App",
                        url = "www.google.com"
                ),
                termsOfService = "All Rights Reserved"
        ),
        servers = {
                @Server(
                        description = "Local Env",
                        url = "http://localhost:8098"
                ),
                @Server(
                        description = "Testing Env",
                        url = "https://testing_demo.cavinkare.in/fintech/"
                )
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Auth Description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {



}
