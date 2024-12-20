package com.jwt.JwtSecurity.config.intializer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryInitializer implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        List<String[]> categories = List.of(
                new String[]{"1", "Coding"},
                new String[]{"2", "Work"},
                new String[]{"3", "DSA Learning"},
                new String[]{"4", "Problem Solving"},
                new String[]{"5", "Personal"},
                new String[]{"6", "Learn New items"},
                new String[]{"7", "Travel"},
                new String[]{"8", "Finance"},
                new String[]{"9", "Fitness"},
                new String[]{"10", "Product Planning"}
        );

        for (String[] category : categories) {
            Long count = (Long) entityManager.createQuery(
                            "SELECT COUNT(c) FROM Category c WHERE c.id = :id")
                    .setParameter("id", Long.parseLong(category[0]))
                    .getSingleResult();

            if (count == 0) {
                Query query = entityManager.createNativeQuery(
                        "INSERT INTO category (id, name) VALUES (:id, :name)");
                query.setParameter("id", Long.parseLong(category[0]));
                query.setParameter("name", category[1]);
                query.executeUpdate();
            }
        }
    }
}