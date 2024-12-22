package com.jwt.JwtSecurity.controller;

import com.jwt.JwtSecurity.config.annotation.FileExtensionCheck;
import com.jwt.JwtSecurity.dto.response.GenericResponse;
import com.jwt.JwtSecurity.enums.FileType;
import com.jwt.JwtSecurity.exception.NotFoundException;
import com.jwt.JwtSecurity.service.iservice.FileService;
import com.jwt.JwtSecurity.utils.AppMessages;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file-manager")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping(value = "/file-upload/{fileType}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<GenericResponse<String>> addFile(@Valid @FileExtensionCheck(ext = "application/pdf, png", isMandatory = true, message = "Invalid format") MultipartFile file,
                          FileType fileType) throws IOException {
        String res =fileService.uploadFile(file, fileType);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, res));
    }

    @GetMapping(value = "/file-download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName, @RequestParam FileType fileType) throws IOException {
        log.info("Downloading the file");
        Resource file = fileService.downloadFile(fileName, fileType);

        if (file == null)
            throw new NotFoundException(fileName + AppMessages.FILE_NOT_FOUND);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContentAsByteArray());
    }

    @DeleteMapping("/delete-file")
    public ResponseEntity<GenericResponse<String>> deleteFile(@RequestParam String fileName, @RequestParam FileType fileType) throws IOException {
        fileService.deleteFile(fileName, fileType);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, "File Deleted Successfully"));
    }
}