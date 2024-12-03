package com.jwt.JwtSecurity.service.iservice;

import com.jwt.JwtSecurity.enums.FileType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadFile(MultipartFile files, FileType fileType) throws IOException;

    Resource downloadFile(String filename, FileType fileType) throws IOException;

    void deleteFile(String filename, FileType fileType) throws IOException;

}
