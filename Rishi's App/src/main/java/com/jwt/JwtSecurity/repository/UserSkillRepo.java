package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.user.UserSkills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSkillRepo extends JpaRepository<UserSkills, Long> {

     Optional<UserSkills> findBySkillAndSkillDomain(String skills, String category);
}
