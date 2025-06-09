package com.eclosia.authservice.service;

import com.eclosia.authservice.dto.KeycloakRoleDto;
import com.eclosia.authservice.dto.TokensDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.eclosia.authservice.dto.UserRegistrationRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j //annotation Lombok qui te permet d'utiliser facilement un logger dans ta classe sans l’écrire toi-même.

public class KeycloakService {

    @Value("${spring.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.keycloak.client-id}")
    private String clientId;

    @Value("${spring.keycloak.realm}")
    private String realm;

    private final WebClient webClient;

    private Mono<String> getAdminAccessToken() {

        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("access_token").asText())
                .onErrorResume(e -> {
                    log.error("Failed to get Keycloak admin access token", e);
                    return Mono.error(e);
                });
    }

    public Mono<String> registerUser(UserRegistrationRequestDTO dto) {

        return getAdminAccessToken()
                .flatMap(token -> webClient.post()
                        .uri("/admin/realms/{realm}/users", realm)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(Map.of(
                                "username", dto.getEmail(),
                                "email", dto.getEmail(),
                                "firstName", dto.getFirstName(),
                                "lastName", dto.getLastName(),
                                "enabled", true,
                                "emailVerified", false,
                                "credentials", List.of(Map.of(
                                        "type", "password",
                                        "value", dto.getPassword(),
                                        "temporary", false
                                ))
                        ))

                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()) {
                                String locationHeader = response.headers().header("Location").stream()
                                        .findFirst()
                                        .orElse(null);
                                log.debug("Location header: {}", locationHeader);
                                if (locationHeader != null) {
                                    String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
                                    log.info("User registered in Keycloak with email: {} and id: {}", dto.getEmail(), userId);
                                    return Mono.just(userId);
                                } else {
                                    return Mono.error(new RuntimeException("Keycloak did not return a Location header"));
                                }
                            } else {
                                return response.bodyToMono(String.class)
                                        .flatMap(errorBody -> Mono.error(new RuntimeException("Keycloak error: " + response.statusCode() + " - " + errorBody)));
                            }
                        })
                );
    }

    public Mono<TokensDTO> loginUser(String email, String password) {
        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("grant_type", "password")
                        .with("username", email)
                        .with("password", password))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    return response.bodyToMono(JsonNode.class)
                            .flatMap(errorJson -> {
                                String errorMessage = errorJson.has("error_description") ? errorJson.get("error_description").asText() : "Login failed";
                                return Mono.error(new RuntimeException(errorMessage));
                            });
                })
                .bodyToMono(JsonNode.class)
                .map(json -> new TokensDTO(json.get("access_token").asText(), json.get("refresh_token").asText()));
    }

    public Mono<TokensDTO> refresh(String refreshToken) {
        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("grant_type", "refresh_token"))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> new TokensDTO(json.get("access_token").asText(), json.get("refresh_token").asText()));
    }

    public Mono<KeycloakRoleDto> getRoleDetails(String roleName, String token) {

             return webClient.get()
                        .uri("/admin/realms/{realm}/roles/{role-name}", realm, roleName)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .map(json -> {
                            KeycloakRoleDto role = new KeycloakRoleDto();
                            role.setId(json.get("id").asText());
                            role.setName(json.get("name").asText());
                            role.setDescription(json.has("description") ? json.get("description").asText() : null);
                            return role;
                        })
                        .onErrorResume(e -> {
                            log.error("Failed to get role details", e);
                            return Mono.error(e);
                        });
    }

    public Mono<Boolean> assignRoleToUser(String userId, String roleName) {

        return getAdminAccessToken()
                .flatMap(token -> {
                    return getRoleDetails(roleName, token)
                            .flatMap(roleDetails -> {
                                return webClient.post()
                                        .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .bodyValue(Collections.singletonList(roleDetails))
                                        .retrieve()
                                        .toBodilessEntity()
                                        .map(response -> {
                                            HttpStatusCode status = response.getStatusCode();
                                            if (status == HttpStatus.NO_CONTENT) {
                                                log.info("Rôle '{}' assigné avec succès à l'utilisateur '{}'", roleName, userId);
                                                return true;
                                            } else {
                                                log.warn("Réponse inattendue lors de l'assignation: {}", status);
                                                return false;
                                            }
                                        });
                            });
                });
    }

    public Mono<Void> triggerEmailVerification(String userId) {
        log.info("trying to send email verification");

        return getAdminAccessToken()
                .flatMap(token -> webClient.put()
                        .uri("/admin/realms/{realm}/users/{userId}/send-verify-email", realm, userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Keycloak error: " + response.statusCode() + " - " + errorBody))))
                        .bodyToMono(Void.class)
                        .doOnSuccess(v -> log.info("Verification email sent for user: {}", userId))
                        .onErrorResume(e -> {
                            log.error("Failed to send verification email for user: {}", userId, e);
                            return Mono.error(e);
                        }));
    }
}
