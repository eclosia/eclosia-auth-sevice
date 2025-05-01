package com.eclosia.authservice.controller;
import com.eclosia.authservice.entitiy.User;

import com.eclosia.authservice.dto.DeletionRequestDTO;
import com.eclosia.authservice.dto.StudentLoginRequestDTO;
import com.eclosia.authservice.dto.StudentRegistrationRequestDTO;
import com.eclosia.authservice.dto.StudentDTO;
import com.eclosia.authservice.service.StudentService;
import jakarta.validation.Valid;
import com.eclosia.authservice.entitiy.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Authentication")

@Slf4j
public class StudentController {
  private final StudentService userService;

  @PostMapping("register")
  public Mono<ResponseEntity<String>> registerUser(@Valid @RequestBody StudentRegistrationRequestDTO dto) {
    log.info(dto.toString());
    log.info("Received request to register user");

    return userService.registerUser(dto)
       .map(newUser -> {
         URI location = UriComponentsBuilder.fromPath("/api/users/{id}")
            .buildAndExpand(newUser.getId())
            .toUri();
         log.info(location.toString());
         return ResponseEntity.created(location).body("User Registered successfully");
       });
  }

  @PostMapping("delete")
  public Mono<ResponseEntity<String>> deleteUser(@Valid @RequestBody DeletionRequestDTO deletionRequestDTO) {

    return ReactiveSecurityContextHolder.getContext()
       .map(SecurityContext::getAuthentication)
       .flatMap(authentication -> {
         if (!authentication.getName().equals(deletionRequestDTO.getEmail())) {
           return Mono.just(ResponseEntity.badRequest().body("Invalid credentials"));
         }
         return userService.deleteUser(deletionRequestDTO.getEmail(), deletionRequestDTO.getPassword(), authentication)
            .map(deletedUser -> ResponseEntity.ok("User Deleted with id: " + deletedUser.getId()))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
       });
  }

  @io.swagger.v3.oas.annotations.Operation(summary = "User login")
  @PostMapping("login")
  public Mono<ResponseEntity<String>> login(@Valid @RequestBody StudentLoginRequestDTO loginRequest){

    return userService.login(loginRequest);

  }
}
