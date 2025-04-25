package com.eclosia.authservice.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.eclosia.authservice.dto.StudentRegistrationRequestDTO;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, StudentRegistrationRequestDTO>{
  @Override
  public void initialize(PasswordMatch p) {
  }
  public boolean isValid(StudentRegistrationRequestDTO student, ConstraintValidatorContext c) {
    String password = student.getPassword();
    String confirmPassword = student.getConfirmPassword();

    if(password == null || !password.equals(confirmPassword)) {
      return false;
    }

    return true;
  }
}
