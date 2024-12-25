package com.jwt.JwtSecurity.dto.request;

import lombok.Getter;

@Getter
public class ForgotPasswordRequest {

    String token;

    String password;
}
