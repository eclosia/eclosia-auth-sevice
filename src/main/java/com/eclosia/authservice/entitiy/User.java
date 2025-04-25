package com.eclosia.authservice.entitiy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import lombok.*;

@Table("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  private Long id;

  @Column("keycloak_id")
  private String keycloakId;

  @Column("first_name")
  private String firstName;

  @Column("last_name")
  private String lastName;

  @Column("gender")
  private String gender;

  @Column("email")
  private String email;

  @Column("deleted_at")
  private Instant deletedAt;

  @Column("created_at")
  private Instant createdAt;

  @Column("updated_at")
  private Instant updatedAt;

  @Column("last_login_at")
  private Instant lastLoginAt;
}
