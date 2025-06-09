package com.eclosia.authservice.controller;

import com.eclosia.authservice.dto.*;
import com.eclosia.authservice.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:4000/")
public class StudentController {

    private final StudentService userService;

    @PostMapping("register")
    public Mono<Object> registerUser(@RequestBody UserRegistrationRequestDTO dto) {

        return userService.registerUser(dto)
                .flatMap(Mono::just);

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

    @PostMapping("login")
    public Mono<ResponseEntity<LoginResponseDTO>> login(@RequestBody UserLoginRequestDTO loginRequest) {

        return userService.login(loginRequest);

    }

    @PostMapping("refresh")
    public Mono<ResponseEntity<LoginResponseDTO>> refresh(@RequestParam String refreshToken) {
        return userService.refresh(refreshToken);
    }
}
