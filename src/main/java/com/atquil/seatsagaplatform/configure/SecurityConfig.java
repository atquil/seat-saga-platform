package com.atquil.seatsagaplatform.configure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                // Disable CSRF for this API-centric service (stateless/session-based hybrid)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 1. Permit Swagger & Static Resources
                        .requestMatchers(
                                "/login.html",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/webjars/**"
                        ).permitAll()
                        // Permiting actuator
                        .requestMatchers("/actuator/**").authenticated()

                        // 2. All other requests require Authentication
                        .anyRequest().authenticated()
                )

                // 3. Configure OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html") // <--- Point to our static file
                        .defaultSuccessUrl("/dashboard.html", true) // <--- Force redirect to dashboard
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )

                // 4. Logout Configuration
                .logout(logout -> logout
                        .logoutSuccessUrl("https://www.youtube.com/channel/UCcl7nrBBvf8ytJhIqWC9kOA")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );


        return http.build();
    }
}