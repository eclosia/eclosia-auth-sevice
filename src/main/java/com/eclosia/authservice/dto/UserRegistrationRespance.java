package com.eclosia.authservice.dto;

import lombok.Data;

@Data
public class UserRegistrationRespance {

    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String access_token;
    private String refresh_token;

}
