package com.jwt.JwtSecurity.service.impl;

import com.jwt.JwtSecurity.enums.FileType;
import com.jwt.JwtSecurity.model.FileManager;
import com.jwt.JwtSecurity.repository.FileManagerRepo;
import com.jwt.JwtSecurity.service.iservice.FileService;
import com.jwt.JwtSecurity.service.iservice.IFilePathService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    Map<String, IFilePathService> filePath;

    @Autowired
    FileManagerRepo fileManagerRepo;


    public FileServiceImpl(List<IFilePathService> filePathList){

        this.filePath = filePathList.stream()
                .collect(Collectors.toMap(path -> path.getClass().getSimpleName(),
                        Function.identity()));

    }

    @Override
    public String uploadFile(MultipartFile files, FileType fileType) throws IOException {

        FileManager entity = new FileManager();

        String filename = System.currentTimeMillis() + "-" + files.getOriginalFilename();
        Path destinationFile;

        Path location = filePath.getOrDefault(fileType.label, null).getDestinationPath();
        destinationFile = location.resolve(Paths.get(filename)).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(location.toAbsolutePath()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error.badFilePath");


        try (InputStream inputStream = files.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        entity.setFileName(filename);
        entity.setFileType(fileType);
        fileManagerRepo.save(entity);

        return filename;
    }

    @Override
    public Resource downloadFile(String filename, FileType fileType) throws IOException {
        Path file = filePath.getOrDefault(fileType.label, null).getDestinationPath().resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable())
            return resource;
        else
            throw new RuntimeException("Not Found " + filename);
    }

    @Override
    public void deleteFile(String filename, FileType fileType) throws IOException {
        Path file = filePath.getOrDefault(fileType.label, null).getDestinationPath().resolve(filename);
        Files.delete(file);
    }

    @PostConstruct
    void createDirectory() {

        filePath.values().forEach(path -> {
            try {
                Files.createDirectories(path.getDestinationPath());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });

        filePath.values()
                .stream()
                .filter(path -> !Files.exists(path.getDestinationPath()))
                .forEach(path -> {
                    try {
                        log.info("Path {} ", path.getDestinationPath());
                        Files.createDirectories(path.getDestinationPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
