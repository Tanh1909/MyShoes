package com.example.security.config.exception;

import com.example.common.data.response.ApiResponse;
import com.example.common.exception.ErrorCode;
import com.example.security.config.constant.AuthErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode e= AuthErrorCode.UNAUTHENTICATED;
        response.setStatus(e.getHttpStatus().value());
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper=new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(e.getCode(),e.getMessage())));
            response.flushBuffer();
        } catch (IOException err) {
            log.error(err.getMessage());
        }
    }
}
