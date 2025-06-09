package com.eclosia.authservice.validation;
import com.eclosia.authservice.enums.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class GenderValidator implements ConstraintValidator<ValidGenderString, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    try {
      Gender.valueOf(value.toUpperCase()); // Case-insensitive validation
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}

