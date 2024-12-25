package com.jwt.JwtSecurity.service.impl;

import com.jwt.JwtSecurity.dto.request.ForgotPasswordRequest;
import com.jwt.JwtSecurity.dto.request.UserSignInRequest;
import com.jwt.JwtSecurity.dto.response.UserTokenResponse;
import com.jwt.JwtSecurity.exception.NotFoundException;
import com.jwt.JwtSecurity.exception.RefreshTokenExpiredException;
import com.jwt.JwtSecurity.model.RefreshToken;
import com.jwt.JwtSecurity.model.user.User;
import com.jwt.JwtSecurity.repository.RefreshTokenRepository;
import com.jwt.JwtSecurity.repository.UserRepo;
import com.jwt.JwtSecurity.security.JwtService;
import com.jwt.JwtSecurity.utils.AppMessages;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthenticationService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtService jwtService;

    @Value("${my.app.refreshExpirationMs}")
    Integer refreshExpirationMs;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    MailService mailService;

    @Autowired
    TemplateServiceImpl templateService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public String generateRefreshToken(User user) {

        Optional<RefreshToken> previousToken = refreshTokenRepository.findByUserId(user.getId());
        previousToken.ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpirydate(Instant.now().plusMillis(refreshExpirationMs));
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();

    }

    public UserTokenResponse login(UserSignInRequest loginRequest) {

        User userDetails = userDetailService.loadUserByUsername(loginRequest.getEmail());

        try {

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails.getEmail(), loginRequest.getPassword(), userDetails.getAuthorities());

            authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            if (usernamePasswordAuthenticationToken.isAuthenticated())
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Incorrect Password");
        }

        String accessToken = jwtService.generateAccessToken(userDetails.getEmail(), userDetails.getAuthorities());
        String refreshToken = generateRefreshToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return UserTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .build();
    }

    public UserTokenResponse getAccessToken(String refreshToken) {
        Optional<RefreshToken> getToken = refreshTokenRepository.findByToken(refreshToken);
        RefreshToken refreshToken1 = verifyRefreshTokenExpiration(getToken
                .orElseThrow(() -> new NotFoundException(AppMessages.REFRESH_NOT_FOUND)));
        User user = userRepo.findById(getToken.get().getUser().getId()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getAuthorities());
        return UserTokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken1.getToken()).build();

    }

    private RefreshToken verifyRefreshTokenExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpirydate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException(AppMessages.REFRESH_TOKEN_EXPIRED);
        }
        return refreshToken;
    }

    public String sendResetLink(String email) throws MessagingException {

        User userDetails = userDetailService.loadUserByUsername(email);
        String token = jwtService.generateJWTTokenForResetPassword(email);
        String template = templateService.getForgotPasswordTemplate(email, userDetails.getUsername(), token);
        mailService.sendForgotPasswordLink(template,"Reset Link", "rishiswag12@gmail.com");

        return token;
    }

    public void resetPassword(ForgotPasswordRequest forgotPasswordRequest) {

        if(jwtService.isTokenExpired(forgotPasswordRequest.getToken()))
            throw new RefreshTokenExpiredException(AppMessages.PASSWORD_RESET_LINK_EXPIRED);

        String email = jwtService.extractUsername(forgotPasswordRequest.getToken());
        User user = userDetailService.loadUserByUsername(email);
        user.setPassword(passwordEncoder.encode(forgotPasswordRequest.getPassword()));
        userRepo.save(user);
    }
}
