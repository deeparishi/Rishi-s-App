package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String refreshToken);

    @Query(value = "SELECT * FROM refresh_token WHERE user_id = :userId", nativeQuery = true)
    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);
}
