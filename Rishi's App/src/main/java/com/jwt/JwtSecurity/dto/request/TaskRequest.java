package com.jwt.JwtSecurity.dto.request;

import com.jwt.JwtSecurity.enums.TaskPriority;
import com.jwt.JwtSecurity.enums.TaskStatus;
import com.jwt.JwtSecurity.model.Category;
import com.jwt.JwtSecurity.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest {

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime dueDate;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate;

    private Long categoryId;
}
