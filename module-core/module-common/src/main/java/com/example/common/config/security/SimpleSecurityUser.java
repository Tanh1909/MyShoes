package com.example.common.config.security;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SimpleSecurityUser {
    private Integer id;
    private String username;
    private List<String> roles;
}
