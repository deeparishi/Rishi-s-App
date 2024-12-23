package com.jwt.JwtSecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jwt.JwtSecurity.enums.TaskPriority;
import com.jwt.JwtSecurity.enums.TaskStatus;
import com.jwt.JwtSecurity.model.task.Category;
import com.jwt.JwtSecurity.model.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime dueDate;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate;

    @JsonIgnore
    private User user;

    private Category category;
}
