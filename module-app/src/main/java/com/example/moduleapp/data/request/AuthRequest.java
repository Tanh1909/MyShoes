package com.example.moduleapp.data.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequest {
    private String fullName;
    private String email;
    private String username;
    private String password;
}