package com.jwt.JwtSecurity.dto.record;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCacheDto{

    private Long id;

    private String title;

    private String description;

    private String createdDate;

    private String dueDate;

    private String priority;

    private String status;

    private Long userId;

    private String userEmail;

    private Long categoryId;

    private String categoryName;

}
