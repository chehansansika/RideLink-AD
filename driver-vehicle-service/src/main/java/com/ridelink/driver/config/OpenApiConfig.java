package com.ridelink.driver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration for the Driver & Vehicle Service.
 *
 * <p>Swagger UI is available at: http://localhost:8082/swagger-ui.html
 * <p>API docs are available at:  http://localhost:8082/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI driverVehicleServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink — Driver & Vehicle Service API")
                        .description("""
                                REST API for managing driver operational profiles, vehicle information,
                                driver availability, service areas, simulated driver locations, and
                                eligible driver discovery for the Ride Management Service.
                                
                                **Service Port:** 8082
                                **Database:** ridelink_driver_db (MongoDB)
                                **Git Branch:** Driver-&-Vehicle-Service
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RideLink — Member 2")
                                .email("siyath.dilsara.3942@gmail.com"))
                        .license(new License()
                                .name("University Project — Internal Use Only")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")));
    }
}
