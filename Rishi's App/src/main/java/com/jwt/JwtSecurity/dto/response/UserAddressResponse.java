package com.jwt.JwtSecurity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressResponse {

    Long houseNumber;

    String streetName;

    Long zipCode;

}
