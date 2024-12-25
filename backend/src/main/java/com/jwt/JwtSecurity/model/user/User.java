package com.jwt.JwtSecurity.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jwt.JwtSecurity.enums.Enums;
import com.jwt.JwtSecurity.model.role.UserRole;
import com.jwt.JwtSecurity.model.task.Task;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "user")
@Entity
@Data
public class User  implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, updatable = false)
    private String loginId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserRole> userRoles;


    @OneToOne(mappedBy = "user")
    @JsonManagedReference(value = "user-address-reference")
    UserAddress userAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "user-posts-reference")
    List<UserPosts> userPosts;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference(value = "user-skills-reference")
    @JoinTable(
            name = "user_skills_combined",
            joinColumns = @JoinColumn(name = "login_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    List<UserSkills> userSkills;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-task-reference")
    private List<Task> tasks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Enums.AuthProvider provider;

    private String providerId;

    private boolean isEmailVerified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRole()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
