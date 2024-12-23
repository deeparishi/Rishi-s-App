package com.jwt.JwtSecurity.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jwt.JwtSecurity.config.redis.RedisConfig;
import com.jwt.JwtSecurity.dto.record.TaskCacheDto;
import com.jwt.JwtSecurity.dto.request.TaskRequest;
import com.jwt.JwtSecurity.enums.TaskStatus;
import com.jwt.JwtSecurity.exception.NotFoundException;
import com.jwt.JwtSecurity.model.task.Task;
import com.jwt.JwtSecurity.repository.CategoryRepo;
import com.jwt.JwtSecurity.repository.TaskRepo;
import com.jwt.JwtSecurity.repository.UserRepo;
import com.jwt.JwtSecurity.security.JwtService;
import com.jwt.JwtSecurity.service.iservice.ITaskService;
import com.jwt.JwtSecurity.utils.AppMessages;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.search.FieldName;
import redis.clients.jedis.search.IndexDefinition;
import redis.clients.jedis.search.IndexOptions;
import redis.clients.jedis.search.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    CacheService<TaskCacheDto> cacheService;

    @Autowired
    TaskRepo taskRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    JwtService jwtService;

    UnifiedJedis jedis;

    TaskServiceImpl(){
        this.jedis = RedisConfig.getJedis();
    }

    @Override
    public TaskCacheDto createTask(TaskRequest request) {

        Task task = new Task();
        task.setCreatedDate(LocalDateTime.now());
        task.setDescription(request.getDescription());
        task.setTitle(request.getTitle());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setTitle(request.getTitle());
        task.setStatus(request.getStatus());
        task.setUser(userRepo.findByEmail(jwtService.extractEmailFromToken())
                .orElseThrow(() -> new NotFoundException(AppMessages.USER_NOT_FOUND)));
        task.setCategory(categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(AppMessages.CATEGORY_NOT_FOUND)));

        Task taskToUpdate = taskRepo.save(task);
        TaskCacheDto taskCacheDto = mapTaskToDto(taskToUpdate);
//        cacheService.save(CacheConstants.TASK_KEY, taskCacheDto, TaskCacheDto.class);

        jedis.jsonArrAppend("tasks", new Path2("$"), getJsonObject(task));
        return taskCacheDto;

    }

    @Override
    @Transactional
    public TaskCacheDto updateTask(Long taskId, TaskRequest request) throws JsonProcessingException {

//        List<TaskCacheDto> tasks = cacheService.getAll(CacheConstants.TASK_KEY, TaskCacheDto.class);
//        List<TaskCacheDto> responses = tasks.stream()
//                .filter(task -> task.getId().equals(taskId))
//                .map(task -> {
//                    taskToUpdate.setCreatedDate(LocalDateTime.now());
//                    taskToUpdate.setDescription(request.getDescription());
//                    taskToUpdate.setTitle(request.getTitle());
//                    taskToUpdate.setDueDate(request.getDueDate());
//                    taskToUpdate.setPriority(request.getPriority());
//                    taskToUpdate.setTitle(request.getTitle());
//                    taskToUpdate.setStatus(request.getStatus());
//                    taskToUpdate.setUser(userRepo.findByEmail(jwtService.extractEmailFromToken())
//                            .orElseThrow(() -> new NotFoundException(AppMessages.USER_NOT_FOUND)));
//                    taskToUpdate.setCategory(categoryRepo.findById(request.getCategoryId())
//                            .orElseThrow(() -> new NotFoundException(AppMessages.TASK_NOT_FOUND)));
//                    taskRepo.save(taskToUpdate);
//                    return mapTaskToDto(taskToUpdate);
//                })
//                .toList();
//        cacheService.delete(CacheConstants.TASK_KEY);
//        cacheService.saveInBulk(CacheConstants.TASK_KEY, responses, TaskCacheDto.class);

        Task taskToUpdate = taskRepo.findById(taskId)
                .orElseThrow(() -> new NotFoundException(AppMessages.TASK_NOT_FOUND));

        taskToUpdate.setCreatedDate(LocalDateTime.now());
        taskToUpdate.setDescription(request.getDescription());
        taskToUpdate.setTitle(request.getTitle());
        taskToUpdate.setDueDate(request.getDueDate());
        taskToUpdate.setPriority(request.getPriority());
        taskToUpdate.setTitle(request.getTitle());
        taskToUpdate.setStatus(request.getStatus());
        taskToUpdate.setUser(userRepo.findByEmail(jwtService.extractEmailFromToken())
                .orElseThrow(() -> new NotFoundException(AppMessages.USER_NOT_FOUND)));
        taskToUpdate.setCategory(categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(AppMessages.TASK_NOT_FOUND)));
        taskRepo.save(taskToUpdate);
        TaskCacheDto dto = mapTaskToDto(taskToUpdate);

        try {
            String query = "$[?(@.id == " + taskId + ")]";
            String result = jedis.jsonSetWithEscape("tasks", new Path2(query), dto);
        } catch (JedisDataException e) {
            jedis.jsonArrAppend("tasks", new Path2("$"), getJsonObject(taskToUpdate));
        }

        taskRepo.save(taskToUpdate);
        return dto;
    }

    @Override
    @Transactional
    public String deleteTask(Long taskId) throws JsonProcessingException {

//        List<TaskCacheDto> tasks = cacheService.getAll(CacheConstants.TASK_KEY, TaskCacheDto.class);
//        List<TaskCacheDto> taskRespectiveToUser = tasks.stream()
//                .filter(task -> !task.getId().equals(taskId))
//                .toList();
//
//        cacheService.delete(CacheConstants.TASK_KEY);
//        cacheService.saveInBulk(CacheConstants.TASK_KEY, taskRespectiveToUser, TaskCacheDto.class);
        String query = "$[?(@.id == " + taskId + ")]";
        long delete = jedis.jsonDel("tasks", new Path2(query));
        taskRepo.deleteById(taskId);
        return "Deleted Sucessfully!";
    }

    @Override
    @Transactional
    public List<TaskCacheDto> getTasksByUser(Long userId) throws JsonProcessingException {

//        List<TaskCacheDto> tasks = cacheService.getAll(CacheConstants.TASK_KEY, TaskCacheDto.class);
//        List<TaskCacheDto> taskRespectiveToUser = tasks.stream()
//                .filter(task -> task.getUserId().equals(userId))
//                .toList();

//        return taskRespectiveToUser;

        String query = "$[?(@.userEmail == \"" + jwtService.extractEmailFromToken() + "\")]";
        Object result = jedis.jsonGet("tasks", new Path2(query));

        if (result != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = result.toString();
                return mapper.readValue(jsonString, new TypeReference<List<TaskCacheDto>>() {
                });
            } catch (Exception e) {
                throw new RuntimeException("Error mapping tasks to DTO", e);
            }
        }

        List<Task> tasks = taskRepo.findByUserId(userId);
        return tasks.stream().map(task -> mapTaskToDto(task)).toList();
    }

    @Override
    @Transactional
    public List<TaskCacheDto> getTasksByStatus(TaskStatus status) throws JsonProcessingException {
//        List<TaskCacheDto> tasks = cacheService.getAll(CacheConstants.TASK_KEY, TaskCacheDto.class);
//        String email = jwtService.extractEmailFromToken();
//        return tasks.stream()
//                .filter(task -> task.getUserEmail().equals(email) && task.getStatus().equalsIgnoreCase(status.name()))
//                .toList();

        String query = "$[?(@.status == \"" + status.name() + "\" && @.userEmail == \"" + jwtService.extractEmailFromToken() + "\" )]";
        Object result = jedis.jsonGet("tasks", new Path2(query));

        if (result != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = result.toString();
                return mapper.readValue(jsonString, new TypeReference<List<TaskCacheDto>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Error mapping tasks to DTO", e);
            }
        }
        return new ArrayList<>();

    }

    @Override
    public List<TaskCacheDto> searchTasks(String keyword) throws JsonProcessingException {

        Object result = jedis.jsonGet("tasks", new Path2("*"));
        String email = jwtService.extractEmailFromToken();
        List<TaskCacheDto> tasks = new ArrayList<>();

        if (result != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = result.toString();
                tasks = mapper.readValue(jsonString, new TypeReference<List<TaskCacheDto>>() {
                });
            } catch (Exception e) {
                throw new RuntimeException("Error mapping tasks to DTO", e);
            }
        }

        List<TaskCacheDto> response = tasks.stream()
                .filter(task -> task.getUserEmail().equals(email))
                .filter(task -> matchTheTasks(task, keyword))
                .toList();

        return response;
    }

    private boolean matchTheTasks(TaskCacheDto task, String keyword) {

        return task.getStatus().equalsIgnoreCase(keyword) ||
                task.getCategoryName().equalsIgnoreCase(keyword) ||
                task.getTitle().equalsIgnoreCase(keyword) ||
                task.getDescription().equalsIgnoreCase(keyword) ||
                task.getStatus().equalsIgnoreCase(keyword) ||
                task.getPriority().equalsIgnoreCase(keyword) ||
                task.getDueDate().equals(task.getDueDate()) ||
                task.getCreatedDate().equals(task.getCreatedDate());
    }

    private TaskCacheDto mapTaskToDto(Task task) {
        return new TaskCacheDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedDate().toString(),
                task.getDueDate().toString(),
                task.getPriority().name(),
                task.getStatus().name(),
                task.getUser().getId(),
                task.getUser().getEmail(),
                task.getCategory().getId(),
                task.getCategory().getName()
        );
    }

    public void addTasksToCache() {
        createTaskIndex();
        List<Task> tasks = taskRepo.findAll();
        JsonArray array = new JsonArray();
        tasks.forEach(task -> array.add(getJsonObject(task)));
        jedis.jsonSet("tasks", new Path2("$"), array);
    }

    private JsonObject getJsonObject(Task task) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", task.getId());
        jsonObject.addProperty("title", task.getTitle());
        jsonObject.addProperty("description", task.getDescription());
        jsonObject.addProperty("status", task.getStatus().name());
        jsonObject.addProperty("priority", task.getPriority().name());
        jsonObject.addProperty("dueDate", task.getDueDate().toString());
        jsonObject.addProperty("createdDate", task.getCreatedDate() != null ? task.getCreatedDate().toString() : null);
        jsonObject.addProperty("userId", task.getUser().getId());
        jsonObject.addProperty("userEmail", task.getUser().getEmail());
        jsonObject.addProperty("categoryId", task.getCategory().getId());
        jsonObject.addProperty("categoryName", task.getCategory().getName());
        return jsonObject;
    }

    public void createTaskIndex() {

        Schema schema = new Schema()
                .addField(new Schema.Field(FieldName.of("$.id").as("id"), Schema.FieldType.NUMERIC, true, false))
                .addField(new Schema.Field(FieldName.of("$.title").as("title"), Schema.FieldType.TEXT, true, false))
                .addField(new Schema.Field(FieldName.of("$.description").as("description"), Schema.FieldType.TEXT, false, false))
                .addField(new Schema.Field(FieldName.of("$.status").as("status"), Schema.FieldType.TEXT, false, false))
                .addField(new Schema.Field(FieldName.of("$.priority").as("priority"), Schema.FieldType.TEXT, false, false))
                .addField(new Schema.Field(FieldName.of("$.dueDate").as("dueDate"), Schema.FieldType.TEXT, false, false))
                .addField(new Schema.Field(FieldName.of("$.createdDate").as("createdDate"), Schema.FieldType.TEXT, false, false))
                .addField(new Schema.Field(FieldName.of("$.userId").as("userId"), Schema.FieldType.NUMERIC, false, false))
                .addField(new Schema.Field(FieldName.of("$.userEmail").as("userEmail"), Schema.FieldType.TEXT, false, false))
                .addField(new Schema.Field(FieldName.of("$.categoryId").as("categoryId"), Schema.FieldType.NUMERIC, true, false))
                .addField(new Schema.Field(FieldName.of("$.categoryName").as("categoryName"), Schema.FieldType.TEXT, false, false));

        IndexDefinition def = new IndexDefinition(IndexDefinition.Type.JSON)
                .setPrefixes(new String[]{"tasks:"});

        jedis.ftCreate("tasksIdx", IndexOptions.defaultOptions().setDefinition(def), schema);
    }
}