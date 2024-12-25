package com.jwt.JwtSecurity.service.iservice;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ICacheService<T> {

    public void save(String key, T item, Class<T> aClass);

    public void saveInBulk(String key, List<T> item, Class<T> aClass);

    public List<T> getAll(String key, Class<T> clazz) throws JsonProcessingException;

    public void delete(String key);

}
