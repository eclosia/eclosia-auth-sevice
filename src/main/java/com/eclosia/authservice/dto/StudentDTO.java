package com.eclosia.authservice.dto;

import com.eclosia.authservice.enums.Gender;
import lombok.Data;
import java.util.Date;
@Data
public class StudentDTO {
  private String firstname;
  private String lastname;
  private String email;
  private String password;
  private String phone;
  private Number numAppoge;
  private Gender gender;
  private Date birthdate;

}
