package com.eclosia.authservice.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface ValidGenderString {
  String message() default "Invalid gender value. Must be 'Male' or 'Female'";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
