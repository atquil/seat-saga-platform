package com.atquil.seatsagaplatform.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF if you are building a stateless API,
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 1. Permit Swagger UI and OpenAPI documentation
                        .requestMatchers(
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 2. All other requests require Google Authentication
                        .anyRequest().authenticated()
                )

                // 3. Configure OAuth2 Login (Google)
                .oauth2Login(Customizer.withDefaults())

                // 4. Configure Logout to redirect to your YouTube Channel
                .logout(logout -> logout
                        .logoutSuccessUrl("https://www.youtube.com/channel/UCcl7nrBBvf8ytJhIqWC9kOA")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );

        return http.build();
    }
}