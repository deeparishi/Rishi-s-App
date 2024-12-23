package com.jwt.JwtSecurity.security.filter;

import com.jwt.JwtSecurity.model.user.User;
import com.jwt.JwtSecurity.security.JwtService;
import com.jwt.JwtSecurity.service.impl.AuthenticationService;
import com.jwt.JwtSecurity.service.impl.UserDetailServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Lazy
    @Autowired
    AuthenticationService authService;

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_TIME = 60000;

    private final Map<String, Integer> failedAttempts = new HashMap<>();

    private final Map<String, Long> blockedIPs = new HashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        if (blockedIPs.containsKey(ip) && (System.currentTimeMillis() - blockedIPs.get(ip) < BLOCK_TIME)) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "IP is blocked.");
            return;
        }

        try {

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            log.info(SecurityContextHolder.getContext().toString());

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = userDetailService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails.getEmail())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set in SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            response.setStatus(345);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired. Please refresh.\"}");
        }

        if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
            failedAttempts.put(ip, failedAttempts.getOrDefault(ip, 0) + 1);
            if (failedAttempts.get(ip) >= MAX_ATTEMPTS) {
                blockedIPs.put(ip, System.currentTimeMillis());
                failedAttempts.put(ip, 0);
            }
        }
    }
}