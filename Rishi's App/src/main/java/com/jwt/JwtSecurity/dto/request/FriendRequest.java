package com.jwt.JwtSecurity.dto.request;

import com.jwt.JwtSecurity.enums.FriendsFrom;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequest {

    private String friendName;

    private String mobileNumber;

    private String emailId;

    private FriendsFrom friendsFrom;

    private String city;

}
