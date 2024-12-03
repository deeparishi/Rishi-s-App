package com.jwt.JwtSecurity.config.annotation;

import com.jwt.JwtSecurity.config.annotation.validator.FileExtensionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FileExtensionValidator.class)
public @interface FileExtensionCheck {

    String message() default "Not a valid format";

    String ext();

    boolean isMandatory();

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
}