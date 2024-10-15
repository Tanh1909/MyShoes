package com.example.common.config.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityContext {
    public static final ThreadLocal<SimpleSecurityUser> context = new ThreadLocal<SimpleSecurityUser>();

    public static void setSimpleSecurityUser(SimpleSecurityUser user) {
        context.set(user);
    }

    public static SimpleSecurityUser getSimpleSecurityUser() {
        return context.get();
    }

    public static void clearContext() {
        context.remove();
    }
}
