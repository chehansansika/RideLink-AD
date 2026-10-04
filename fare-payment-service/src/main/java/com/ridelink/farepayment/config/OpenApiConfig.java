package com.ridelink.farepayment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI farePaymentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink - Fare & Payment Service API")
                        .description("REST API documentation for Fare estimation, final fare calculation, simulated payment processing, and receipt management in RideLink.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RideLink Backend Team")
                                .email("dev@ridelink.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
