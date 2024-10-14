package com.example.common.data.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SimpleSecurityUser {
    private Integer id;
    private String username;
    private List<String> roles;
}
