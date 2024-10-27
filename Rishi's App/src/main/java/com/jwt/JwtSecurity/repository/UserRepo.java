package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT id FROM user ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Long findRecentId();
}
