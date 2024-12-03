package com.jwt.JwtSecurity.exception;

import com.jwt.JwtSecurity.dto.response.GenericResponse;
import com.jwt.JwtSecurity.utils.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<GenericResponse<Object>> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException ex) {
        GenericResponse<Object> response = GenericResponse.error(
                ErrorCodes.fromCode("401").toHttpStatus().toString(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<GenericResponse<Object>> handleRefreshExpiredException(RefreshTokenExpiredException ex) {
        GenericResponse<Object> response = GenericResponse.error(
                ErrorCodes.fromCode("401").toHttpStatus().toString(),
                "Token has expired. Please refresh your token or log in again."
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Object>> handleMethodArgumentException(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        GenericResponse<Object> response = GenericResponse.error(
                HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMap.values().stream().toList()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<GenericResponse<Object>> handleMethodArgumentException(HandlerMethodValidationException exception) {

        GenericResponse<Object> response = GenericResponse.error(
                HttpStatus.BAD_REQUEST.getReasonPhrase(), exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GenericResponse<Object>> handleNotFoundException(NotFoundException exception){
        GenericResponse<Object> response = GenericResponse.error(HttpStatus.NOT_FOUND.getReasonPhrase(), exception.getMessage());
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GenericResponse<Object>> handleBadCredentialException(BadCredentialsException exception){
        GenericResponse<Object> response = GenericResponse.error(HttpStatus.UNAUTHORIZED.getReasonPhrase(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}