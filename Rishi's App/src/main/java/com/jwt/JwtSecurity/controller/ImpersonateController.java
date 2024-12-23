package com.jwt.JwtSecurity.controller;

import com.jwt.JwtSecurity.config.ImpersonationContext;
import com.jwt.JwtSecurity.model.user.User;
import com.jwt.JwtSecurity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
@Slf4j
public class ImpersonateController {

    @Autowired
    private UserRepo userRepo; // Your service to manage users

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/impersonate/{userId}")
    public ResponseEntity<String> impersonateUser(@PathVariable Long userId,
                                                  Authentication authentication) {


        User currentUser = (User) authentication.getPrincipal();
//        if (!currentUser.getRole().equals(Enums.Role.ADMIN)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("You are not authorized to impersonate users.");
//        }

        User userToImpersonate = userRepo.findById(userId).
                orElseThrow(() -> new UsernameNotFoundException("Not found"));

        if (userToImpersonate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        log.info("Impersonated is - {} ", userToImpersonate.getEmail());

        UsernamePasswordAuthenticationToken impersonatedAuth =
                new UsernamePasswordAuthenticationToken(userToImpersonate, null, userToImpersonate.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(impersonatedAuth);

        log.info("Impersonated is - {} ", userToImpersonate.getUsername());
        return ResponseEntity.ok("Impersonating user: " + userToImpersonate.getUsername());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        if (ImpersonationContext.getOriginalAuthentication() != null) {
            Authentication originalAuth = ImpersonationContext.getOriginalAuthentication();
            SecurityContextHolder.getContext().setAuthentication(originalAuth);
            ImpersonationContext.clear();
        }

        SecurityContextHolder.clearContext();

        // Redirect to admin page
        try {
            response.sendRedirect("/admin"); // Adjust the URL as necessary for your admin page
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build(); // Optionally return a 200 response
    }


}
