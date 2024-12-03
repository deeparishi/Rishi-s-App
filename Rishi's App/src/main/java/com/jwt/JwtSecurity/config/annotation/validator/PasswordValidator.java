package com.jwt.JwtSecurity.config.annotation.validator;

import com.jwt.JwtSecurity.config.annotation.ValidatePassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;



public class PasswordValidator implements ConstraintValidator<ValidatePassword, String> {

    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null)
            return false;
        return password.matches(PASSWORD_REGEX);
    }
}
