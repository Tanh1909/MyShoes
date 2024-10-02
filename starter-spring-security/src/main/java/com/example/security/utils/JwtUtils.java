package com.example.security.utils;

import com.example.security.data.response.UserDetailResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtUtils {
    private String secret;
    private Long tokenExpiration;
    private Long refreshExpiration;

    @PostConstruct
    public void init() {
        log.info("init JwtUtils");
        log.info("secret: " + secret);
        log.info("tokenExpiration: " + tokenExpiration);
        log.info("refreshExpiration: " + refreshExpiration);
    }

    public String generateToken(UserDetailResponse user, boolean isRefresh) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (isRefresh ? refreshExpiration : tokenExpiration));
        if (isRefresh) {
            return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .compact();
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope", getScope(user));
        claims.put("userId", user.getId());
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secret)
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .addClaims(claims)
                .compact();
    }

    public Claims getBody(String token) {
        try {
            return Jwts.parser().setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public String getSubject(String token) {
        Claims claims = getBody(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }

    public String getIdJwt(String token) {
        Claims claims = getBody(token);
        if (claims != null) {
            return claims.getId();
        }
        return null;
    }

    public String getScope(UserDetailResponse user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        user.getRoles().stream().forEach(role -> stringJoiner.add(role));
        return stringJoiner.toString();
    }

    public Long getExpiration(String token) {
        Claims claims = getBody(token);
        if (claims != null) {
            long dateIssue = claims.getIssuedAt().getTime();
            long dateExpiration = claims.getExpiration().getTime();
            return dateExpiration - dateIssue;
        }
        return null;
    }

}