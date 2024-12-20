package com.jwt.JwtSecurity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.JwtSecurity.dto.response.GenericResponse;
import com.jwt.JwtSecurity.service.iservice.ICacheService;
import com.jwt.JwtSecurity.utils.AppMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cache")
public class CacheController<T> {

    @Autowired
    ICacheService<T> cacheService;

    @GetMapping("/read-cache/{key}/{className}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<GenericResponse<T>> addMyFriendsInBulkMode(@PathVariable String key, @PathVariable String className) throws JsonProcessingException, ClassNotFoundException {
        Class<T> clazz = (Class<T>) Class.forName(className);
        List<T> response = cacheService.getAll(key, clazz);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, response));
    }

    @PostMapping("/trial")
    public void trial() throws JsonProcessingException, ClassNotFoundException {

    }
}
