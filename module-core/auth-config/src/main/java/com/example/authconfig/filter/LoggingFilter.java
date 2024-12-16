package com.example.authconfig.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@Component
@Order(HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("================Request Method: {}", request.getMethod());
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request Body: {}", getPayload(request));
//        log.info("Headers:");
//        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
//                log.info("{}: {}", headerName, request.getHeader(headerName)));
        filterChain.doFilter(request, response);
    }

    public String getPayload(HttpServletRequest request) throws IOException {
        StringBuilder payload = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                payload.append(line);
//            }
//        } catch (IOException e) {
//            log.error("error extract body from request: {}", e.getMessage());
//        }
        return payload.toString();
    }
}
