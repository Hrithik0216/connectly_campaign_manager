package com.connectly_cm.Connectly_CM.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Allow access to OAuth2 endpoints
                        .requestMatchers("/login/gmailCallback", "/login/oauth2/code/google", "/login/gmail").permitAll()
                        // Secure all other endpoints
                        .anyRequest().authenticated()
                )
                // Disable CSRF protection (required for OAuth2 callbacks)
                .csrf(csrf -> csrf.disable())
                // Disable sessions for stateless APIs (optional)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}