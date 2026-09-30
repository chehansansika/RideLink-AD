package com.ridelink.account.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RideLink - Account Service API",
                version = "1.0.0",
                description = "Account Microservice for RideLink Ride-Sharing Platform. Provides user registration, JWT authentication, profile management, and account lookup services.",
                contact = @Contact(
                        name = "RideLink Development Team - Member 1",
                        email = "support@ridelink.com"
                )
        ),
        security = {
                @SecurityRequirement(name = "BearerAuth")
        }
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter JWT Bearer token to access protected endpoints"
)
public class OpenApiConfig {
}
