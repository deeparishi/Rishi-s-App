package com.jwt.JwtSecurity.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.jwt.JwtSecurity.service.iservice.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Service
public class CacheService <T> implements ICacheService<T> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void save(String key, T item, Class<T> aClass) {
        try {

            Object cachedData = redisTemplate.opsForValue().get(key);
            List<T> currentList;

            if (Objects.nonNull(cachedData)) {
                currentList = objectMapper.readValue(
                        objectMapper.writeValueAsString(cachedData),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, aClass)
                );
            } else {
                currentList = new ArrayList<>();
            }
            currentList.add(item);
            redisTemplate.opsForValue().set(key, currentList, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveInBulk(String key, List<T> item, Class<T> aClass) {

        try {

            Object cachedData = redisTemplate.opsForValue().get(key);
            List<T> currentList;

            if (Objects.nonNull(cachedData))
                currentList = objectMapper.readValue(
                        objectMapper.writeValueAsString(cachedData),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, aClass)
                );
            else
                currentList = new ArrayList<>();

            currentList.addAll(item);
            redisTemplate.opsForValue().set(key, currentList, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> getAll(String key, Class<T> clazz) throws JsonProcessingException {

        Object cachedData = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(cachedData))
            return null;

        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(cachedData),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (MismatchedInputException e) {
            T singleItem = objectMapper.readValue(
                    objectMapper.writeValueAsString(cachedData),
                    clazz
            );
            return List.of(singleItem);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}