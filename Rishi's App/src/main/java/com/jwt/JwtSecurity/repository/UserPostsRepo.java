package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.user.UserPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostsRepo extends JpaRepository<UserPosts, Long> {

}
