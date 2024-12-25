package com.jwt.JwtSecurity.service.iservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.JwtSecurity.dto.record.TaskCacheDto;
import com.jwt.JwtSecurity.dto.request.TaskRequest;
import com.jwt.JwtSecurity.enums.TaskStatus;

import java.util.List;

public interface ITaskService {

    TaskCacheDto createTask(TaskRequest request);

    TaskCacheDto updateTask(Long taskId, TaskRequest request) throws JsonProcessingException;

    String deleteTask(Long taskId) throws JsonProcessingException;

    List<TaskCacheDto> getTasksByUser(Long userId) throws JsonProcessingException;

    List<TaskCacheDto> getTasksByStatus(TaskStatus status) throws JsonProcessingException;

    List<TaskCacheDto> searchTasks(String keyword) throws JsonProcessingException;

    void addTasksToCache();
}
