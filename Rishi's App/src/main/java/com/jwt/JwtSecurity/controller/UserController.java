package com.jwt.JwtSecurity.controller;

import com.jwt.JwtSecurity.dto.request.UserSkillsRequest;
import com.jwt.JwtSecurity.dto.response.UserDetailsResponse;
import com.jwt.JwtSecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public UserDetailsResponse userDetails(@PathVariable("id") Long id){
        return userService.userDetails(id);
    }

    @PostMapping("/add-posts/{id}")
    public UserDetailsResponse userPosts(@PathVariable("id") Long id, @RequestBody List<String> posts){
        return userService.addPostsToUser(posts, id);
    }

    @PostMapping("/add-skill-to-user/{id}")
    public UserDetailsResponse userSkill(@PathVariable("id") Long id,@RequestBody List<UserSkillsRequest> newSkills){
        return userService.addSkillToUser(id, newSkills);
    }

}
