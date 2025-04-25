package com.eclosia.authservice.dto;
import com.eclosia.authservice.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@PasswordMatch
public class DeletionRequestDTO {
  @Email
  @NotBlank(message = "Email is mandatory")
  private String email;

  @NotBlank(message = "Password is mandatory")
  @Size(min = 6, max = 40, message = "Password must be between 8 and 40 characters")
  private String password;

  @NotBlank(message = "Confirm password is mandatory")
  private String confirmPassword;
}
