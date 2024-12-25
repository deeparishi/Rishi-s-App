package com.jwt.JwtSecurity.security.oauth2;

import com.jwt.JwtSecurity.enums.Enums;
import com.jwt.JwtSecurity.model.role.Role;
import com.jwt.JwtSecurity.model.user.User;
import com.jwt.JwtSecurity.repository.UserRepo;
import com.jwt.JwtSecurity.security.oauth2.factory.OAuth2UserFactory;
import com.jwt.JwtSecurity.security.oauth2.userInfo.OAuth2UserInfo;
import com.jwt.JwtSecurity.service.impl.UserService;
import com.jwt.JwtSecurity.service.iservice.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepo userRepo;

    private final IRoleService roleService;

    @Autowired
    UserService userService;

    public OAuthUserService(UserRepo userRepo, IRoleService roleService) {
        this.userRepo = userRepo;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> getUserFromOAuthReq = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = getUserFromOAuthReq.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserFactory.getOAuth2User(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        if (oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found for the oauth user");
        }

        Optional<User> optionalUser = userRepo.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            Enums.AuthProvider authProvider = Enums.AuthProvider.valueOf(
                    oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()
            );

            if (!user.getProvider().equals(authProvider)) {
                throw new OAuth2AuthenticationException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            updateExistingUser(user, oAuth2UserInfo);
        } else {
            registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return oAuth2User;
    }

    private void registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setProvider(Enums.AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        updateUserDetails(user, oAuth2UserInfo);
        user.setLoginId(userService.generateLoginId());
        setRoles(user);

        userRepo.save(user);
    }

    private void updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        if (existingUser.getUserRoles() == null || existingUser.getUserRoles().isEmpty()) {
            setRoles(existingUser);
        }
        updateUserDetails(existingUser, oAuth2UserInfo);
        userRepo.save(existingUser);
    }

    private void updateUserDetails(User user, OAuth2UserInfo oAuth2UserInfo) {
        user.setUsername(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setEmailVerified((boolean) oAuth2UserInfo.getAttributes().get("email_verified"));
    }

    private void setRoles(User user) {
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleService.getRoleByName(Enums.Role.USER));
        roleService.giveRolesToUser(user, userRoles);
    }
}
