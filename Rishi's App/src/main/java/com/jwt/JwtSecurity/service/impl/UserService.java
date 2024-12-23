package com.jwt.JwtSecurity.service.impl;

import com.jwt.JwtSecurity.dto.record.UserPostsRecord;
import com.jwt.JwtSecurity.dto.record.UserSkillRecord;
import com.jwt.JwtSecurity.dto.request.UserSignUpRequest;
import com.jwt.JwtSecurity.dto.request.UserSkillsRequest;
import com.jwt.JwtSecurity.dto.response.UserAddressResponse;
import com.jwt.JwtSecurity.dto.response.UserDetailsResponse;
import com.jwt.JwtSecurity.dto.response.UserTokenResponse;
import com.jwt.JwtSecurity.enums.Enums;
import com.jwt.JwtSecurity.exception.NotFoundException;
import com.jwt.JwtSecurity.model.role.Role;
import com.jwt.JwtSecurity.model.user.User;
import com.jwt.JwtSecurity.model.user.UserAddress;
import com.jwt.JwtSecurity.model.user.UserPosts;
import com.jwt.JwtSecurity.model.user.UserSkills;
import com.jwt.JwtSecurity.repository.UserAddressRepo;
import com.jwt.JwtSecurity.repository.UserPostsRepo;
import com.jwt.JwtSecurity.repository.UserRepo;
import com.jwt.JwtSecurity.repository.UserSkillRepo;
import com.jwt.JwtSecurity.security.JwtService;
import com.jwt.JwtSecurity.service.iservice.IRoleService;
import com.jwt.JwtSecurity.utils.AppMessages;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserAddressRepo userAddressRepo;

    @Autowired
    UserPostsRepo userPostsRepo;

    @Autowired
    UserSkillRepo userSkillRepo;

    @Autowired
    IRoleService roleService;


    public UserTokenResponse register(UserSignUpRequest users) {

        User user = new User();
        user.setUsername(users.getUsername());
        user.setEmail(users.getEmail());
        user.setPassword(passwordEncoder.encode(users.getPassword()));
        user.setLoginId(generateLoginId());
        UserAddress userAddress = mapUserAddress(users.getAddressDto(), user);
        user.setUserAddress(userAddress);
        user.setProvider(Enums.AuthProvider.LOCAL);
        user.setProviderId("LOCAL");
        user.setEmailVerified(false);
        List<Role> roles = new ArrayList<>();
        roles.add(roleService.getRoleByName(Enums.Role.USER));
        roleService.giveRolesToUser(user, roles);
        userRepo.save(user);
        return getUserTokenResponse(user);
    }

    private UserTokenResponse getUserTokenResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getAuthorities());
        String refreshToken = authenticationService.generateRefreshToken(user);

        return UserTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private UserAddress mapUserAddress(UserAddressResponse addressDto, User user) {

        UserAddress userAddressBuilder = UserAddress.builder()
                .houseNumber(addressDto.getHouseNumber())
                .streetName(addressDto.getStreetName())
                .zipCode(addressDto.getZipCode())
                .user(user)
                .build();
        return userAddressRepo.save(userAddressBuilder);
    }

    public UserDetailsResponse userDetails(Long id) {

        User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException(AppMessages.USER_NOT_FOUND));
        List<UserSkillRecord> skills = new ArrayList<>(mapSkillsToUser(user.getUserSkills()));
        List<UserPostsRecord> posts = new ArrayList<>(mapPostsToUser(user.getUserPosts()));

        return UserDetailsResponse.builder()
                .email(user.getEmail())
//                .role(user.getRole())
                .loginId(user.getLoginId())
                .userAddress(mapAddress(user.getUserAddress()))
                .userPosts(posts)
                .userSkills(skills)
                .build();
    }

    public UserAddressResponse mapAddress(UserAddress userAddress) {
        return UserAddressResponse.builder()
                .zipCode(userAddress.getZipCode())
                .streetName(userAddress.getStreetName())
                .houseNumber(userAddress.getHouseNumber())
                .build();
    }

    public UserDetailsResponse addPostsToUser(List<String> userPosts, Long id) {

        User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException(AppMessages.USER_NOT_FOUND));

        List<UserPosts> posts = new ArrayList<>();

        userPosts.forEach(post -> {
            UserPosts userPost = new UserPosts();
            userPost.setPosts(post);
            userPost.setUser(user);
            posts.add(userPost);
        });

        userPostsRepo.saveAll(posts);
        return userDetails(user.getId());
    }

    public List<UserPostsRecord> mapPostsToUser(List<UserPosts> posts) {
        List<UserPostsRecord> records = new ArrayList<>();
        posts.forEach(post -> records.add(new UserPostsRecord(post.getId(), post.getPosts())));
        return records;
    }


    public UserDetailsResponse addSkillToUser(Long userId, List<UserSkillsRequest> newSkills) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(AppMessages.USER_NOT_FOUND));

        for (UserSkillsRequest newSkill : newSkills) {
            UserSkills skill = userSkillRepo.findBySkillAndSkillDomain(newSkill.getSkill(), newSkill.getCategory())
                    .orElseGet(() -> {
                        UserSkills userSkill = new UserSkills();
                        userSkill.setSkillDomain(newSkill.getCategory());
                        userSkill.setSkill(newSkill.getSkill());
                        userSkillRepo.save(userSkill);
                        return userSkill;
                    });

            if (!user.getUserSkills().contains(skill)) {
                user.getUserSkills().add(skill);
            }
        }

        return userDetails(userRepo.save(user).getId());
    }

    public List<UserSkillRecord> mapSkillsToUser(List<UserSkills> userSkills) {
        List<UserSkillRecord> records = new ArrayList<>();
        userSkills.forEach(skill -> records.add(new UserSkillRecord(skill.getId(), skill.getSkillDomain(),
                skill.getSkill())));
        return records;
    }

    public String generateLoginId() {
        Long id = userRepo.findRecentId();
        return id == null ? "USER ".concat(String.valueOf(0)) : "USER ".concat(String.valueOf(id));
    }

    public UserDetailsResponse mapUserToResponse(User user) {

        List<UserSkillRecord> skills = new ArrayList<>(mapSkillsToUser(user.getUserSkills()));
        List<UserPostsRecord> posts = new ArrayList<>(mapPostsToUser(user.getUserPosts()));

        return UserDetailsResponse.builder()
                .email(user.getEmail())
//                .role(user.getRole())
                .loginId(user.getLoginId())
                .userAddress(mapAddress(user.getUserAddress()))
                .userPosts(posts)
                .userSkills(skills)
                .build();
    }

    public UserDetailsResponse findByLoginId(String loginId) {

        return userRepo.findByLoginId(loginId)
                .map(user -> {
                    List<UserSkillRecord> skills = new ArrayList<>(mapSkillsToUser(user.getUserSkills()));
                    List<UserPostsRecord> posts = new ArrayList<>(mapPostsToUser(user.getUserPosts()));
                    return UserDetailsResponse.builder()
                            .email(user.getEmail())
//                            .role(user.getRole())
                            .loginId(user.getLoginId())
                            .userAddress(mapAddress(user.getUserAddress()))
                            .userPosts(posts)
                            .userSkills(skills)
                            .build();
                })
                .orElseThrow(() -> new NotFoundException(AppMessages.USER_NOT_FOUND));
    }

    @Transactional
    public List<User> findAll() {
        log.info("---Fetching From User Database---");
        return userRepo.findAll();

    }

}