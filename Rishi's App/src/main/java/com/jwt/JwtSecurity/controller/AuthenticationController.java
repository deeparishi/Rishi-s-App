package com.jwt.JwtSecurity.controller;

import com.jwt.JwtSecurity.dto.request.ForgotPasswordRequest;
import com.jwt.JwtSecurity.dto.request.UserSignInRequest;
import com.jwt.JwtSecurity.dto.request.UserSignUpRequest;
import com.jwt.JwtSecurity.dto.response.GenericResponse;
import com.jwt.JwtSecurity.dto.response.UserTokenResponse;
import com.jwt.JwtSecurity.service.impl.AuthenticationService;
import com.jwt.JwtSecurity.service.impl.UserService;
import com.jwt.JwtSecurity.utils.AppMessages;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GenericResponse<UserTokenResponse>> register(@Valid @RequestBody UserSignUpRequest user) {
        UserTokenResponse response = userService.register(user);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<UserTokenResponse>> login(@RequestBody UserSignInRequest signInRequest) {
        UserTokenResponse response = authenticationService.login(signInRequest);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @GetMapping("/getToken/{refreshToken}")
    public ResponseEntity<GenericResponse<UserTokenResponse>> getToken(@PathVariable("refreshToken") String refreshToken) {
        UserTokenResponse response = authenticationService.getAccessToken(refreshToken);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @GetMapping("/reset-link/{email}")
    public GenericResponse<Object> sendResetLink(@PathVariable String email) throws MessagingException {
        String token = authenticationService.sendResetLink(email);
        return GenericResponse.success(AppMessages.PASSWORD_RESET_LINK_SENT, token);
    }

    @PutMapping("/reset-password")
    public GenericResponse<Object> resetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws MessagingException {
        authenticationService.resetPassword(forgotPasswordRequest);
        return GenericResponse.success(AppMessages.PASSWORD_CHANGED);
    }
}