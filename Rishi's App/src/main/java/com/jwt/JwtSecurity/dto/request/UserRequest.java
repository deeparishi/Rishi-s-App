package com.jwt.JwtSecurity.dto.request;

import com.jwt.JwtSecurity.dto.response.UserAddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    String username;

    String password;

    String email;

    String role;

    UserAddressResponse addressDto;
}
