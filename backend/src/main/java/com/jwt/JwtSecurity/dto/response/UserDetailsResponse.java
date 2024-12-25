package com.jwt.JwtSecurity.dto.response;

import com.jwt.JwtSecurity.dto.record.UserPostsRecord;
import com.jwt.JwtSecurity.dto.record.UserSkillRecord;
import com.jwt.JwtSecurity.enums.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loginId;

    private String username;

    private String email;

    private Enums.Role role;

    UserAddressResponse userAddress;

    List<UserPostsRecord> userPosts;

    List<UserSkillRecord> userSkills;

}
