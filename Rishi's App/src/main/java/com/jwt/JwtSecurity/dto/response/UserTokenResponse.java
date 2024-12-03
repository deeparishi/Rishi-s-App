package com.jwt.JwtSecurity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenResponse {

    String accessToken;

    String refreshToken;

    private List<String> roles;

}
