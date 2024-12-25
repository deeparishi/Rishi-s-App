package com.jwt.JwtSecurity.service.iservice;

import com.jwt.JwtSecurity.enums.Enums;
import com.jwt.JwtSecurity.model.role.Role;
import com.jwt.JwtSecurity.model.user.User;

import java.util.List;

public interface IRoleService {

    public Role getRoleByName(Enums.Role roleName);

    public void giveRolesToUser(User user, List<Role> roles);

}
