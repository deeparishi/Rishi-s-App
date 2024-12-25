package com.jwt.JwtSecurity.config.annotation;

import com.jwt.JwtSecurity.config.annotation.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidatePassword {

    String message() default "Password must be between 8 to 16 characters, contain at least one digit, one uppercase letter, one lowercase letter, and one special character.";
    Class<?>[] groups() default {};  // Can be used to group validation constraints

    Class<? extends Payload>[] payload() default {};
}
