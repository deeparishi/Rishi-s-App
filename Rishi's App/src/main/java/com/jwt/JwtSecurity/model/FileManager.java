package com.jwt.JwtSecurity.model;

import com.jwt.JwtSecurity.config.audit.AuditorEntity;
import com.jwt.JwtSecurity.enums.FileType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "file_manager")
@Getter
@Setter
public class FileManager extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "file_type")
    FileType fileType;

}
