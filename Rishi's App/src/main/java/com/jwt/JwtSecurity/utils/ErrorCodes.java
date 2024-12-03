package com.jwt.JwtSecurity.utils;

import org.springframework.http.HttpStatus;

public enum ErrorCodes {

    INTERNAL_SERVER_ERROR("500"),
    NOT_FOUND("404"),
    OK("200"),
    UNAUTHORIZED("401");

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus toHttpStatus() {
        return HttpStatus.valueOf(Integer.parseInt(code));
    }

    public static ErrorCodes fromCode(String code) {
        for (ErrorCodes statusCode : values()) {
            if (statusCode.code.equals(code)) {
                return statusCode;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }

}