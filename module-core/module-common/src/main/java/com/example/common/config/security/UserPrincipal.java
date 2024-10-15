package com.example.common.config.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPrincipal {
    private SimpleSecurityUser userInfo;
    private String uri;
    private String method;
    private String clientId;
}
