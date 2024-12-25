package com.jwt.JwtSecurity.service.impl;

import com.jwt.JwtSecurity.exception.NotFoundException;
import com.jwt.JwtSecurity.model.user.User;
import com.jwt.JwtSecurity.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).orElseThrow(() -> new NotFoundException("User Not found"));
        return user;
    }
}
