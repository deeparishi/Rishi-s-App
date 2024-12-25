package com.jwt.JwtSecurity.enums;

public class Enums {

    public enum AuthProvider {
        LOCAL,
        GITHUB,
        GOOGLE
    }

    public enum Role {
        ADMIN,
        USER,
        MODERATOR
    }
}