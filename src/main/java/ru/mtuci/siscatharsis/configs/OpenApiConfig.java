package ru.mtuci.siscatharsis.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings({"unused"})
@Configuration
public class OpenApiConfig {

        private static final String SECURITY_SCHEME_NAME = "bearerAuth";

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .components(new Components()
                                .addSecuritySchemes("bearerAuth", createBearerScheme()))
                        .info(new Info()
                                .title("Siscatharsis API docs")
                                .version("1.0.0"));
        }

        private SecurityScheme createBearerScheme() {
                return new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Provide the JWT token. Example: `Bearer eyJhbGciOi...`");
        }


        @Bean
        public GroupedOpenApi adminApi() {
                return GroupedOpenApi.builder()
                        .group("admin")
                        .pathsToMatch("/admin/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                        .group("public")
                        .pathsToMatch("/**")
                        .pathsToExclude("/admin/**")
                        .build();
        }
}