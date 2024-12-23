package com.jwt.JwtSecurity.intializer;

import com.jwt.JwtSecurity.model.role.Role;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleInitializer  implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        Long count = (Long) entityManager.createQuery("SELECT COUNT(r) FROM Role r")
                .getSingleResult();

        if (count == 0) {
            List<Role> roles = List.of(
                    new Role(1, "USER"),
                    new Role(2, "ADMIN"),
                    new Role(3, "MODERATOR")
            );

            for (Role role : roles) {
                entityManager.persist(role);
            }
        }
    }
}