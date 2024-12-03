package com.jwt.JwtSecurity.dto.request;

import com.jwt.JwtSecurity.config.annotation.ValidatePassword;
import com.jwt.JwtSecurity.dto.response.UserAddressResponse;
import com.jwt.JwtSecurity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSignUpRequest {

    @NotBlank
    String username;

//    @ValidatePassword
    String password;

    @Email
    String email;

    Role role;

    UserAddressResponse addressDto;
}
