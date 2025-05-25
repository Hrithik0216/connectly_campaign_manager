package com.connectly_cm.Connectly_CM.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConnectlySecurityConfig {
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authReq -> authReq
                                /*Made the below apis (used for connecting Inbox account) as public api's.
                                 * Will check and move to authenticated apis*/
                                .requestMatchers("/login/gmailCallback",
                                        "/login/oauth2/code/google",
                                        "/login/gmail",
                                        "/api/email/**").permitAll()
                                /*Made the below apis (used for connecting Crm account) as public api's.
                                 * Will check and move to authenticated apis*/
                                .requestMatchers("/sendEmail", "buyContacts/**",
                                        "/crm/**").permitAll()
                                .anyRequest().authenticated()
                )

                /*Cross site theft protection disabled*/
                .csrf(csrf -> csrf.disable())

                /*Stateless as every data is sent through api headers to server with request*/
                .sessionManagement(sessions ->
                        sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
