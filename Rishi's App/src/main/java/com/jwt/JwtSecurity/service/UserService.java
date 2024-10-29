package com.jwt.JwtSecurity.service;

import com.jwt.JwtSecurity.dto.record.UserPostsRecord;
import com.jwt.JwtSecurity.dto.record.UserSkillRecord;
import com.jwt.JwtSecurity.dto.request.UserSkillsRequest;
import com.jwt.JwtSecurity.dto.response.UserAddressResponse;
import com.jwt.JwtSecurity.dto.response.UserDetailsResponse;
import com.jwt.JwtSecurity.dto.request.UserRequest;
import com.jwt.JwtSecurity.dto.response.UserTokenResponse;
import com.jwt.JwtSecurity.enums.Role;
import com.jwt.JwtSecurity.model.User;
import com.jwt.JwtSecurity.model.UserAddress;
import com.jwt.JwtSecurity.model.UserPosts;
import com.jwt.JwtSecurity.model.UserSkills;
import com.jwt.JwtSecurity.repository.UserAddressRepo;
import com.jwt.JwtSecurity.repository.UserPostsRepo;
import com.jwt.JwtSecurity.repository.UserRepo;
import com.jwt.JwtSecurity.repository.UserSkillRepo;
import com.jwt.JwtSecurity.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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


    public UserTokenResponse register(UserRequest users) {

        User user = new User();
        user.setUsername(users.getUsername());
        user.setEmail(users.getEmail());
        user.setPassword(passwordEncoder.encode(users.getPassword()));
        user.setRole(Role.valueOf(users.getRole()));
        user.setLoginId(generateLoginId());
        UserAddress userAddress = mapUserAddress(users.getAddressDto(), user);
        user.setUserAddress(userAddress);
        userRepo.save(user);
        return getUserTokenResponse(user);
    }

    private UserTokenResponse getUserTokenResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
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

    public UserDetailsResponse userDetails(Long id){

        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        List<UserSkillRecord> skills = new ArrayList<>(mapSkillsToUser(user.getUserSkills()));
        List<UserPostsRecord> posts = new ArrayList<>(mapPostsToUser(user.getUserPosts()));

        return UserDetailsResponse.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .loginId(user.getLoginId())
                .userAddress(mapAddress(user.getUserAddress()))
                .userPosts(posts)
                .userSkills(skills)
                .build();
    }

    public UserAddressResponse mapAddress(UserAddress userAddress){
        return UserAddressResponse.builder()
                .zipCode(userAddress.getZipCode())
                .streetName(userAddress.getStreetName())
                .houseNumber(userAddress.getHouseNumber())
                .build();
    }

    public UserDetailsResponse addPostsToUser(List<String> userPosts, Long id){

        User user = userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Not found"));

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
                .orElseThrow(() -> new RuntimeException("User not found"));

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

    private String generateLoginId(){
        Long id = userRepo.findRecentId();
        return id == null ? "USER".concat(String.valueOf(0)): "USER".concat(String.valueOf(id));
    }


}
