package com.jwt.JwtSecurity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.JwtSecurity.dto.record.TaskCacheDto;
import com.jwt.JwtSecurity.dto.request.TaskRequest;
import com.jwt.JwtSecurity.dto.response.GenericResponse;
import com.jwt.JwtSecurity.enums.TaskStatus;
import com.jwt.JwtSecurity.service.iservice.ITaskService;
import com.jwt.JwtSecurity.utils.AppMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskManagerController {

    @Autowired
    ITaskService taskService;

    @PostMapping("/create-task")
    public ResponseEntity<GenericResponse<TaskCacheDto>> createTasks(@RequestBody TaskRequest taskRequest) {
        TaskCacheDto response = taskService.createTask(taskRequest);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @GetMapping("/get-tasks/{id}")
    public ResponseEntity<GenericResponse<TaskCacheDto>> getTasks(@PathVariable Long id) throws JsonProcessingException {
        List<TaskCacheDto> response = taskService.getTasksByUser(id);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @GetMapping("/get-tasks-by-status/{taskStatus}")
    public ResponseEntity<GenericResponse<TaskCacheDto>> getTasksByStatus(@PathVariable TaskStatus taskStatus) throws JsonProcessingException {
        List<TaskCacheDto> response = taskService.getTasksByStatus(taskStatus);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @GetMapping("/search-tasks/{keyword}")
    public ResponseEntity<GenericResponse<TaskCacheDto>> searchTasks(@PathVariable String keyword) throws JsonProcessingException {
        List<TaskCacheDto> response = taskService.searchTasks(keyword);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @PutMapping("/update-tasks/{taskId}")
    public ResponseEntity<GenericResponse<TaskCacheDto>> updateTask(@PathVariable Long taskId, @RequestBody TaskRequest taskRequest) throws JsonProcessingException {
        TaskCacheDto response = taskService.updateTask(taskId, taskRequest);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @DeleteMapping("delete-task/{taskId}")
    public ResponseEntity<GenericResponse<String>> deleteTask(@PathVariable Long taskId) throws JsonProcessingException {
        String response = taskService.deleteTask(taskId);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @GetMapping("/add-tasks-to-cache")
    public void addCacheToRedis(){
        taskService.addTasksToCache();
    }
}
