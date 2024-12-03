package com.jwt.JwtSecurity.controller;

import com.jwt.JwtSecurity.dto.request.UserSkillsRequest;
import com.jwt.JwtSecurity.dto.response.GenericResponse;
import com.jwt.JwtSecurity.dto.response.UserDetailsResponse;
import com.jwt.JwtSecurity.service.impl.UserService;
import com.jwt.JwtSecurity.utils.AppMessages;
import com.jwt.JwtSecurity.utils.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<UserDetailsResponse>> userDetails(@PathVariable("id") Long id) {
        UserDetailsResponse response = userService.userDetails(id);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @PostMapping("/add-posts/{id}")
    public ResponseEntity<GenericResponse<UserDetailsResponse>> userPosts(@PathVariable("id") Long id, @RequestBody List<String> posts) {
        UserDetailsResponse response = userService.addPostsToUser(posts, id);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @PostMapping("/add-skill-to-user/{id}")
    public ResponseEntity<GenericResponse<UserDetailsResponse>> userSkill(@PathVariable("id") Long id, @RequestBody List<UserSkillsRequest> newSkills) {
        UserDetailsResponse response = userService.addSkillToUser(id, newSkills);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }
}