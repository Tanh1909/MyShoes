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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class CustomAccessDenied implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorCode errorCode = AuthErrorCode.UNAUTHORIZED;
        response.setStatus(errorCode.getHttpStatus().value());
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(errorCode.getCode(), errorCode.getMessage())));
            response.flushBuffer();
        } catch (IOException err) {
            log.error(err.getMessage());
        }
    }
}