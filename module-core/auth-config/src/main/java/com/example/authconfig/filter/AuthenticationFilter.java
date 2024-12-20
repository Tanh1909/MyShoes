package com.example.authconfig.filter;

import com.example.authconfig.config.constant.AuthErrorCode;
import com.example.authconfig.config.exception.UnauthenticatedException;
import com.example.authconfig.config.exception.UnauthorizedException;
import com.example.authconfig.utils.JwtUtils;
import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.context.SecurityContext;
import com.example.common.context.SimpleSecurityUser;
import com.example.common.context.UserPrincipal;
import com.example.common.data.response.ApiResponse;
import com.example.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
@Order(0)
public class AuthenticationFilter extends OncePerRequestFilter {
    @Qualifier("publicEndpoints")
    private final List<String> PUBLIC_ENDPOINTS;

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("AuthenticationFilter Invoked");
        String token = getTokenFromRequest(request);
        try {
            if (PUBLIC_ENDPOINTS.stream().anyMatch(request.getRequestURI()::contains)&&token==null) {
                log.debug("API PUBLIC MATCH WITH URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
            } else {
                if (token != null && jwtUtils.getBody(token) != null) {
                    SimpleSecurityUser simpleSecurityUser = jwtUtils.getUser(token);
                    UserPrincipal userPrincipal = UserPrincipal.builder()
                            .userInfo(simpleSecurityUser)
                            .uri(request.getRequestURI())
                            .method(request.getMethod())
                            .clientId(request.getRemoteAddr())
                            .build();
                    SecurityContext.setContext(userPrincipal);
                    filterChain.doFilter(request, response);
                } else {
                    throw new UnauthenticatedException();
                }
            }
        } catch (UnauthenticatedException ex) {
            log.error("UNAUTHENTICATED WITH {}: {}", request.getMethod(), request.getRequestURI());
            ErrorCode errorCode = AuthErrorCode.UNAUTHENTICATED;
            ApiResponse.writeResponseError(response, errorCode);
        } catch (UnauthorizedException ex) {
            log.error("UNAUTHORIZED WITH {}: {}", request.getMethod(), request.getRequestURI());
            ErrorCode e = AuthErrorCode.UNAUTHORIZED;
            ApiResponse.writeResponseError(response, e);
        } catch (Exception ex) {
            log.error("SOMETHING WRONG WITH {}: {}", request.getMethod(), request.getRequestURI(), ex);
            ErrorCode e = ErrorCodeBase.INTERNAL_SERVER_ERROR;
            ApiResponse.writeResponseError(response, e);
        } finally {
            log.debug("CLEAR CONTEXT");
            SecurityContext.clearContext();
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        if (authToken != null && authToken.startsWith("Bearer ")) {
            return authToken.substring(7);
        }
        return null;
    }
}
