package com.jwt.JwtSecurity.model;

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
    User user;


}
