package com.jwt.JwtSecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

@Configuration
public class ImpersonationContext {
    private static final ThreadLocal<Authentication> originalAuth = new ThreadLocal<>();

    public static void setOriginalAuthentication(Authentication auth) {
        originalAuth.set(auth);
    }

    public static Authentication getOriginalAuthentication() {
        return originalAuth.get();
    }

    public static void clear() {
        originalAuth.remove();
    }
}

