package com.example.security.config.filter;

import com.example.security.config.service.UserDetailImpl;
import com.example.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null) {
            Claims claims = jwtUtils.getBody(token);
            if (claims != null) {
                String jwtId = claims.getId();
                UserDetailImpl userDetail = new UserDetailImpl();
                userDetail.setUsername(claims.getSubject());
                String[] scopes = ((String) claims.get("scope")).trim().split(" ");
                Set<GrantedAuthority> authorities = Arrays.stream(scopes).map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toSet());
                userDetail.setAuthorities(authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        if (authToken != null && authToken.startsWith("Bearer ")) {
            return authToken.substring(7);
        }
        return null;
    }
}