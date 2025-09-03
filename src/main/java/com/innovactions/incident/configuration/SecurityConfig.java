package com.innovactions.incident.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Slack won’t send CSRF tokens
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/slack/events").permitAll() // allow Slack events
                        .anyRequest().authenticated() // everything else needs auth
                )
                .httpBasic(httpBasic -> {}); // enable HTTP Basic with default settings
        return http.build();
    }
}

