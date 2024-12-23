package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

    @Query(value = "SELECT * FROM task WHERE user_id = ?1", nativeQuery = true)
    List<Task> findByUserId(Long userId);
}
