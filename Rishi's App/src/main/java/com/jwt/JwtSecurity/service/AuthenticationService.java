package com.jwt.JwtSecurity.service;

import com.jwt.JwtSecurity.dto.request.UserRequest;
import com.jwt.JwtSecurity.dto.response.UserTokenResponse;
import com.jwt.JwtSecurity.model.RefreshToken;
import com.jwt.JwtSecurity.model.User;
import com.jwt.JwtSecurity.repository.RefreshTokenRepository;
import com.jwt.JwtSecurity.repository.UserRepo;
import com.jwt.JwtSecurity.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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

    public UserTokenResponse login(UserRequest user) {
        User users = userRepo.findByEmail(user.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Mot Found"));
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = generateRefreshToken(users);
        return UserTokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public UserTokenResponse getAccessToken(String refreshToken) {
        Optional<RefreshToken> getToken = refreshTokenRepository.findByToken(refreshToken);
        RefreshToken refreshToken1 = verifyRefreshTokenExpiration(getToken.get());
        User user = userRepo.findById(getToken.get().getUser().getId()).get();
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        return UserTokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken1.getToken()).build();

    }

    public RefreshToken verifyRefreshTokenExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpirydate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Token Expired please login");
        }
        return refreshToken;
    }
}
