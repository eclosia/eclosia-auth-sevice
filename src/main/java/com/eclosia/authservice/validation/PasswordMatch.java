package com.eclosia.authservice.validation;
import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

public @interface PasswordMatch {
  String message() default "Password and Confirm Password do not match";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
