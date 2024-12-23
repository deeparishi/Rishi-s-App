package com.jwt.JwtSecurity.enums;

public class Enums {

    public enum AuthProvider {
        LOCAL,
//        google,
        GITHUB,
        GOOGLE
    }

    public enum Role {
        ADMIN,
        USER,
        MODERATOR
    }
}