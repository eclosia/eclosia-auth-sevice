package com.eclosia.authservice.repository;

import com.eclosia.authservice.entitiy.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
@Repository
public interface ReactiveUserRepository extends ReactiveCrudRepository <User, Long> {

  Mono<User> findByEmail(String email);
  Mono<Boolean> existsByEmail(String email);

  Mono<User> findByEmailAndDeletedAtNull(String email);
  Mono<User> deleteByEmail(String email);
  Mono<User> findByKeycloakId(String uuid);
  Flux<User> findAllByDeletedAtNull();
  default Mono<User> softDeleteByEmail(String email, SecureRandom secureRandom) {
    return findByEmail(email)
       .flatMap(user -> {
         if (user == null || user.getDeletedAt() != null) {
           return Mono.empty();
         }

         user.setDeletedAt(Instant.now());
         user.setEmail(user.getEmail() + "_deleted_" + generateSecureRandomString(6, secureRandom));
         return save(user);
       });
  }
  private static String generateSecureRandomString(int length, SecureRandom secureRandom) {
    byte[] randomBytes = new byte[length];
    secureRandom.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }
}
