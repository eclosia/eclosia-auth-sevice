package com.eclosia.authservice.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.eclosia.authservice.dto.UserRegistrationRequestDTO;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRegistrationRequestDTO>{
  @Override
  public void initialize(PasswordMatch p) {
  }
  public boolean isValid(UserRegistrationRequestDTO student, ConstraintValidatorContext c) {
    String password = student.getPassword();
    String confirmPassword = student.getPassword();

    if(password == null || !password.equals(confirmPassword)) {
      return false;
    }

    return true;
  }
}
