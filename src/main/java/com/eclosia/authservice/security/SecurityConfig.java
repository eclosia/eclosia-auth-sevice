package com.eclosia.authservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    return http
       .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF
       .authorizeExchange(auth -> auth
          .pathMatchers("/auth/**").permitAll() // Public endpoints
          .anyExchange().authenticated() // Everything else requires authentication
       )
       .build();
  }
}
