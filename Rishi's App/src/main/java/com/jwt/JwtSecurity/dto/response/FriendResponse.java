package com.jwt.JwtSecurity.dto.response;

import com.jwt.JwtSecurity.enums.FriendsFrom;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendResponse {

    private String friendName;

    private String mobileNumber;

    private String emailId;

    private FriendsFrom friendsFrom;

    private String city;

    private LocalDate connectedFrom;

}
