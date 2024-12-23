package com.jwt.JwtSecurity.config;

import com.jwt.JwtSecurity.enums.Enums;
import com.jwt.JwtSecurity.repository.UserRepo;
import com.jwt.JwtSecurity.security.JwtService;
import com.jwt.JwtSecurity.security.entrypoint.CustomAuthenticationEntryPoint;
import com.jwt.JwtSecurity.security.entrypoint.RestAuthenticationEntryPoint;
import com.jwt.JwtSecurity.security.filter.JwtAuthFilter;
import com.jwt.JwtSecurity.security.handler.CustomAccessDeniedHandler;
import com.jwt.JwtSecurity.security.handler.OAuth2LoginSuccessHandler;
import com.jwt.JwtSecurity.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.jwt.JwtSecurity.security.oauth2.OAuthUserService;
import com.jwt.JwtSecurity.service.impl.UserDetailServiceImpl;
import com.jwt.JwtSecurity.service.iservice.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Autowired
    JwtService jwtService;

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    UserRepo userRepo;

    @Autowired
    IRoleService roleService;

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_TIME = 60000; // 1 minute block

    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<String, Long> blockedIPs = new HashMap<>();
    private final List<String> WHITELISTED_ENDPOINTS = List.of("/authenticate-user/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/error");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( req ->
                                req.requestMatchers(WHITELISTED_ENDPOINTS.toArray(String[]::new))
                                .permitAll()
                                .requestMatchers("/demoControl/**").hasAuthority(Enums.Role.USER.name())
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authEndpoint ->
                                authEndpoint
                                        .authorizationRequestRepository(oAuth2AuthorizationRequestRepository))
                        .redirectionEndpoint(redirect ->
                                redirect
                                        .baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo ->
                                userInfo
                                        .userService(oAuth2UserService())
                        )
                        .successHandler(new OAuth2LoginSuccessHandler(jwtService, userRepo, oAuth2AuthorizationRequestRepository))
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .userDetailsService(userDetailService)
                .sessionManagement(session ->  session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new OAuthUserService(userRepo, roleService);
    }
}
