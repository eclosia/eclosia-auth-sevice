package com.eclosia.authservice.configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KeycloakConfig {
  @Value("${spring.keycloak.server-url}")
  private String keycloakServerUrl;

  @Bean
  public WebClient keycloakWebClient(WebClient.Builder builder) {
    return builder.baseUrl(keycloakServerUrl)
       .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
       .build();
  }
}
