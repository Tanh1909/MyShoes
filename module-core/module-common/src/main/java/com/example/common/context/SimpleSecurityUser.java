package com.example.common.context;

import lombok.Data;

import java.util.List;

@Data
public class SimpleSecurityUser {
    private Integer id;
    private String username;
    private List<String> roles;
}
