package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.task.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
}
