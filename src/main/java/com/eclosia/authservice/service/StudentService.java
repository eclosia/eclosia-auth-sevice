package com.eclosia.authservice.service;

import com.eclosia.authservice.dto.LoginResponseDTO;
import com.eclosia.authservice.dto.UserLoginRequestDTO;
import com.eclosia.authservice.dto.UserRegistrationRequestDTO;
import com.eclosia.authservice.entitiy.User;
import com.eclosia.authservice.repository.ReactiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final ReactiveUserRepository userRepository;
    private final KeycloakService keycloakService;

    private Mono<User> userMono;

    public Mono<Object> registerUser(UserRegistrationRequestDTO dto) {
        return userRepository.existsByEmail(dto.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("User with email already exists"));
                    }
                    return keycloakService.registerUser(dto)
                            .flatMap(KeycloakId -> {
                                return keycloakService.assignRoleToUser(KeycloakId, dto.getRole() )
                                        .flatMap(roleAssigned -> {
                                            if (roleAssigned) {

                                                User user = new User();
                                                user.setKeycloakId(KeycloakId); // Store Keycloak user ID
                                                user.setFirstName(dto.getFirstName());
                                                user.setLastName(dto.getLastName());
                                                user.setGender(String.valueOf(dto.getGender()));
                                                user.setEmail(dto.getEmail());
                                                user.setPhone(dto.getPhone());
                                                user.setField(dto.getField());
                                                user.setStudyLevel(dto.getStudyLevel());
                                                user.setBirthDate(dto.getBirthDate());
                                                user.setNumAppoge(dto.getNumAppoge());
                                                user.setRole(dto.getRole());

                                                return userRepository.save(user);
                                            } else {
                                                return Mono.<Object>error(new IllegalStateException("Failed to assign role to user"));
                                            }
                                        });
                            });
                });
    }


    public Mono<User> deleteUser(String email, String password, Authentication authentication) {

        return userMono;

    }


    public Mono<ResponseEntity<LoginResponseDTO>> login(UserLoginRequestDTO dto) {
        return keycloakService.loginUser(dto.getEmail() , dto.getPassword())
                .map(tokens -> ResponseEntity
                        .ok(new LoginResponseDTO(tokens.getAccess_token(), tokens.getRefresh_token(), "success")))
                .onErrorResume(e ->
                     Mono.just(ResponseEntity
                             .status(HttpStatus.UNAUTHORIZED)
                            .body(new LoginResponseDTO(null, null, "login failed")))
                );
    }

    public Mono<ResponseEntity<LoginResponseDTO>> refresh(String refreshToken) {
        return keycloakService.refresh(refreshToken)
                .map(tokens -> ResponseEntity
                        .ok(new LoginResponseDTO(tokens.getAccess_token(), tokens.getRefresh_token(), "success")))
                .onErrorResume(e ->
                        Mono.just(ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(new LoginResponseDTO(null, null, "login failed")))
                );
    }
}
