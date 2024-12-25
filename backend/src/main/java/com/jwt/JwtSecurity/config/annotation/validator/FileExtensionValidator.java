package com.jwt.JwtSecurity.config.annotation.validator;

import com.jwt.JwtSecurity.config.annotation.FileExtensionCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class FileExtensionValidator implements ConstraintValidator<FileExtensionCheck, MultipartFile> {

    private String extension;
    private boolean isMandatory;

    @Override
    public void initialize(FileExtensionCheck fileExtensionCheck) {
        this.extension = fileExtensionCheck.ext();
        this.isMandatory = fileExtensionCheck.isMandatory();
    }


    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null)
            return !isMandatory;

        List<String> allowedExtension = Arrays.stream(extension.split(","))
                .map(String::trim)
                .toList();
        log.info(file.getContentType());

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Invalid file format. Allowed formats are: " + String.join(", ", allowedExtension))
                .addConstraintViolation();

        return allowedExtension.contains(file.getContentType());
    }
}
