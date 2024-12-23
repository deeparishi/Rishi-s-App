package com.jwt.JwtSecurity.service.impl;

import com.jwt.JwtSecurity.enums.Enums;
import com.jwt.JwtSecurity.exception.NotFoundException;
import com.jwt.JwtSecurity.model.role.Role;
import com.jwt.JwtSecurity.model.role.UserRole;
import com.jwt.JwtSecurity.model.user.User;
import com.jwt.JwtSecurity.repository.RoleRepo;
import com.jwt.JwtSecurity.service.iservice.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {

    @Autowired
    RoleRepo roleRepo;

    @Override
    public Role getRoleByName(Enums.Role roleName) {
        return roleRepo.findByRole(roleName.name())
                .orElseThrow(() -> new NotFoundException("Role Not Found! Please Contact Admin"));

    }

    @Override
    public void giveRolesToUser(User user, List<Role> roles) {

        List<UserRole> userRoles = roles.stream()
                .map(role -> UserRole.builder()
                        .role(role)
                        .userInfo(user)
                        .build())
                .collect(Collectors.toList());
        user.setUserRoles(userRoles);
    }
}
