package com.eclosia.authservice.dto;

import com.eclosia.authservice.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

import lombok.Data;


@Data
@Validated
public class UserRegistrationRequestDTO {

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;


    @NotBlank(message = "Date Of Birth is mandatory")
    private Date birthDate;

    @NotBlank(message = "NumAppoge is mandatory")
    private Number numAppoge;

    @Email
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Phone Number is mandatory")
    private String phone;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    private String role;

    private Gender gender;

    private String field;

    private String studyLevel;
}