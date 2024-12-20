package com.jwt.JwtSecurity.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "user_skills")
@Data
public class UserSkills {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_domain")
    String skillDomain;

    @Column(name = "skill")
    String skill;

    @ManyToMany(mappedBy = "userSkills")
    @JsonBackReference(value = "user-skills-reference")
    List<User> user;

}
