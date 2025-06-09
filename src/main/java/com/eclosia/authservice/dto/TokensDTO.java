package com.eclosia.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokensDTO {

    private String access_token;
    private String refresh_token;

}