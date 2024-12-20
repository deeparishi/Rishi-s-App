package com.jwt.JwtSecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GenericResponse<T> {

    private boolean status;

    private String message;

    private String errorType;

    private List<String> messages;

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();

    private T data;

    private List<T> dataa;

    private String statusCode;

    public static <T> GenericResponse<T> success(T data) {

        return GenericResponse.<T>builder()
                .message("Response Success")
                .data(data)
                .errorType("NONE")
                .status(true)
                .build();
    }

    public static <T> GenericResponse<T> success(String message, T data) {

        return GenericResponse.<T>builder()
                .message(message)
                .data(data)
                .errorType("NONE")
                .status(true)
                .build();
    }

    public static <T> GenericResponse<T> success(String message, List<T> data) {

        return GenericResponse.<T>builder()
                .message(message)
                .dataa(data)
                .errorType("NONE")
                .status(true)
                .build();
    }




    public static <T> GenericResponse<T> error(String errorType, String message) {
        return GenericResponse.<T>builder()
                .message(message)
                .errorType(errorType)
                .status(false)
                .build();
    }

    public static <T> GenericResponse<T> error(String errorType, List<String> message) {
        return GenericResponse.<T>builder()
                .messages(message)
                .errorType(errorType)
                .status(false)
                .build();
    }

    public static <T> GenericResponse<T> error(String statusCode, String errorType, String message) {
        return GenericResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .errorType(errorType)
                .status(false)
                .build();
    }
}
