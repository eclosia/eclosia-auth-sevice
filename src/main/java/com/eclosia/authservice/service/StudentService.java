package com.eclosia.authservice.service;

import com.eclosia.authservice.dto.StudentLoginRequestDTO;
import com.eclosia.authservice.dto.StudentRegistrationRequestDTO;
import com.eclosia.authservice.entitiy.User;
import com.eclosia.authservice.repository.ReactiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
  public Mono<User> registerUser(StudentRegistrationRequestDTO dto) {
    return userRepository.existsByEmail(dto.getEmail())
       .flatMap(exists -> {
         if (exists) {
           return Mono.error(new IllegalArgumentException("User with email already exists"));
         }
         return keycloakService.registerUser(dto)
            .flatMap(keycloakUserId -> {
              User user = new User();
              user.setFirstName(dto.getFirstName());
              user.setLastName(dto.getLastName());
              user.setGender(String.valueOf(dto.getGender()));
              user.setEmail(dto.getEmail());
              user.setKeycloakId(keycloakUserId); // Store Keycloak user ID

              return userRepository.save(user)
                 .then(keycloakService.triggerEmailVerification(keycloakUserId)) // Trigger email verification
                 .thenReturn(user);
            });
       });
  }



  public Mono<User> deleteUser(String email, String password, Authentication authentication) {

    Mono<User> deletedUser = userRepository.deleteByEmail(email);

    return deletedUser;

  }


  public Mono<ResponseEntity<String>> login(StudentLoginRequestDTO dto) {
    return keycloakService.loginUser(dto)
       .map(token -> ResponseEntity.ok()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
          .body("Login successful"))
       .onErrorResume(e -> {
         System.err.println("Login failed: " + e.getMessage());
         return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
       });
  }

}
