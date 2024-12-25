package com.jwt.JwtSecurity.service.files;

import com.jwt.JwtSecurity.config.properties.StorageProperties;
import com.jwt.JwtSecurity.service.iservice.IFilePathService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ExcelFilePathService implements IFilePathService {

    private final Path fileLocation;

    public ExcelFilePathService(StorageProperties properties) {
        this.fileLocation = Paths.get(properties.getBaseLocation())
                .resolve(properties.getExcelLocation());
    }

    @Override
    public Path getDestinationPath() {
        return this.fileLocation;
    }

}
