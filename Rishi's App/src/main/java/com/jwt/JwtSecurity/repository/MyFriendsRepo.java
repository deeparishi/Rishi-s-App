package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.MyFriendsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyFriendsRepo extends JpaRepository<MyFriendsInfo, Long> {

}
