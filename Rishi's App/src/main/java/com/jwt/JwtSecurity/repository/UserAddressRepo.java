package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepo extends JpaRepository<UserAddress, Long> {



}
