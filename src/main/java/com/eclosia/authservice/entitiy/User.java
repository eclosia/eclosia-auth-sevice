package com.eclosia.authservice.entitiy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.Date;

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

  @Column("birthDate")
  private Date birthDate;

  @Column("numAppoge")
  private Number numAppoge;

  @Column("gender")
  private String gender;

  @Column("email")
  private String email;

  @Column("phone")
  private String phone;

  @Column("field")
  private String field;

  @Column("study_level")
  private String studyLevel;

  @Column("role")
  private String role;

  @Column("deleted_at")
  private Instant deletedAt;

  @Column("created_at")
  private Instant createdAt;

  @Column("updated_at")
  private Instant updatedAt;

  @Column("last_login_at")
  private Instant lastLoginAt;
}
