package com.pireaus.todoWebApp.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI todoAppOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager Pro API")
                        .version("v1")
                        .description(
                                "REST API for the Task Manager Pro todo application. " +
                                "Auth is a session cookie from POST /login " +
                                "(application/x-www-form-urlencoded: username, password) - " +
                                "log in once from a client that keeps cookies (e.g. Postman) " +
                                "before calling any endpoint below other than /register or /login."
                        )
                );
    }
}
