package com.jwt.JwtSecurity.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demoControl")
@Hidden
public class DemoController {

    @GetMapping("/demoControl")
    public String demo(){
        return "Welcome to secured point";
    }
}
