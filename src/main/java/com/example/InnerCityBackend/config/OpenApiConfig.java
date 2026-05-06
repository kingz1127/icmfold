package com.example.InnerCityBackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Outreach API")
                        .version("1.2.0")
                        .description("Outreach management platform API")
                        .contact(new Contact()
                                .name("Oshunyingbo Adedeji")
                                .url("https://your-website.com")
                                .email("osunyingboadedeji1@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("https://icmfold.onrender.com/api/v1")
                                .description("Production Server"),
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("Local Development")
                ));
    }
}