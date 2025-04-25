package com.eclosia.authservice.controller;

import com.eclosia.authservice.dto.DeletionRequestDTO;
import com.eclosia.authservice.dto.StudentLoginRequestDTO;
import com.eclosia.authservice.dto.StudentRegistrationRequestDTO;
import com.eclosia.authservice.dto.StudentDTO;
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
public class StudentController {

}
