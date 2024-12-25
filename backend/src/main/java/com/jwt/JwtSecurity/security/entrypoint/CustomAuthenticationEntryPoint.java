package com.jwt.JwtSecurity.security.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.JwtSecurity.dto.response.GenericResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        GenericResponse<Object> re = new GenericResponse<>();
        re.setMessage(authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream stream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(stream, re);
        stream.flush();

    }
}
