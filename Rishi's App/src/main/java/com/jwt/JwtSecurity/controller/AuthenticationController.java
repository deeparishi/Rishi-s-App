package com.jwt.JwtSecurity.controller;

import com.jwt.JwtSecurity.dto.request.UserRequest;
import com.jwt.JwtSecurity.dto.response.UserTokenResponse;
import com.jwt.JwtSecurity.service.AuthenticationService;
import com.jwt.JwtSecurity.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate-user")
@Tag(name = "Authentication")
public class AuthenticationController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserTokenResponse register(@RequestBody UserRequest user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public UserTokenResponse login(@RequestBody UserRequest user){
        return authenticationService.login(user);
    }

    @GetMapping("/getToken/{refreshToken}")
    public UserTokenResponse getToken(@PathVariable("refreshToken") String refreshToken){
        return authenticationService.getAccessToken(refreshToken);
    }

}
