package com.atquil.seatsagaplatform.configure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger/OpenAPI to support Google OAuth2 Authentication.
 * This allows the "Authorize" button in Swagger UI to perform the Google login flow.
 * * @author atquil
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("google_auth", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Google OAuth2 Authentication")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("https://accounts.google.com/o/oauth2/v2/auth")
                                                .tokenUrl("https://oauth2.googleapis.com/token")
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID Connect identity")
                                                        .addString("profile", "User profile information")
                                                        .addString("email", "User email address"))))))
                .addSecurityItem(new SecurityRequirement().addList("google_auth"));
    }
}