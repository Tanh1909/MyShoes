package com.example.common.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityContext {
    public static final ThreadLocal<UserPrincipal> context = new ThreadLocal<>();

    public static void setContext(UserPrincipal userPrincipal) {
        context.set(userPrincipal);
    }

    public static UserPrincipal getUserPrincipal() {
        return context.get();
    }

    public static void clearContext() {
        context.remove();
    }
}
