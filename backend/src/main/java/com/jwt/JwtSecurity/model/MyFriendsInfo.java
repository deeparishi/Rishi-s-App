package com.jwt.JwtSecurity.model;

import com.jwt.JwtSecurity.enums.FriendsFrom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "my_friends_info")
@Getter
@Setter
public class MyFriendsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String friendName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "email")
    private String emailId;

    @Column(name = "met_at")
    @Enumerated(EnumType.STRING)
    private FriendsFrom friendsFrom;

    @Column(name = "city")
    private String city;

    @Column(name = "connected_from", updatable = false)
    private LocalDate connectedFrom = LocalDate.now();

    @Column(name = "delete_status")
    private boolean deleteStatus;

}
