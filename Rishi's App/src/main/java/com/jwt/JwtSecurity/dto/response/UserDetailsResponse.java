package com.jwt.JwtSecurity.dto.response;

import com.jwt.JwtSecurity.dto.record.UserPostsRecord;
import com.jwt.JwtSecurity.dto.record.UserSkillRecord;
import com.jwt.JwtSecurity.enums.Role;
import com.jwt.JwtSecurity.model.UserPosts;
import com.jwt.JwtSecurity.model.UserSkills;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsResponse {

    private String loginId;

    private String username;

    private String email;

    private Role role;

    UserAddressResponse userAddress;

    List<UserPostsRecord> userPosts;

    List<UserSkillRecord> userSkills;

}
