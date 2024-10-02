package com.example.security.data.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreationRequest {
    private String username;
    private String password;
}