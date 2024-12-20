package com.jwt.JwtSecurity.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_posts")
@Data
public class UserPosts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "posts")
    String posts;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "login_id")
    @JsonBackReference(value = "user-posts-reference")
    User user;


}
