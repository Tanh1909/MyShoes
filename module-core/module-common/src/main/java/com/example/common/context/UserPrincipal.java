package com.example.common.context;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPrincipal {
    private SimpleSecurityUser userInfo;
    private String uri;
    private String method;
    private String clientId;
}
