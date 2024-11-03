package com.example.authconfig.utils;

import com.example.authconfig.config.properties.JwtProperties;
import com.example.common.context.SimpleSecurityUser;
import com.example.common.utils.JsonUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Component
@Log4j2
@Data
@RequiredArgsConstructor
public class JwtUtils {
    private final JwtProperties jwtProperties;

    @PostConstruct
    public void init() {
        log.info("init JwtUtils");
        log.info("secret: " + jwtProperties.getSecret());
        log.info("tokenExpiration: " + jwtProperties.getTokenExpiration());
        log.info("refreshExpiration: " + jwtProperties.getRefreshExpiration());
    }

    public String generateToken(SimpleSecurityUser user, boolean isRefresh) {
        Long refreshExpiration = jwtProperties.getRefreshExpiration();
        Long tokenExpiration = jwtProperties.getTokenExpiration();
        String secret = jwtProperties.getSecret();
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
        claims.put("user", JsonUtils.encode(user));
        claims.put("scope", getScope(user));
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secret)
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .addClaims(claims)
                .compact();
    }

    public SimpleSecurityUser getUser(String token) {
        Claims claims = getBody(token);
        if (claims != null) {
            return JsonUtils.decode(claims.get("user", String.class), SimpleSecurityUser.class);
        }
        return null;
    }

    public Claims getBody(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Extract boy error {}", e.getMessage());
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


    public String getScope(SimpleSecurityUser user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        user.getRoles().forEach(stringJoiner::add);
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