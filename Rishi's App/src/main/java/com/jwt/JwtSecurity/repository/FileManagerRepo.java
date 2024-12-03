package com.jwt.JwtSecurity.repository;

import com.jwt.JwtSecurity.model.FileManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileManagerRepo extends JpaRepository<FileManager, Long> {

}
