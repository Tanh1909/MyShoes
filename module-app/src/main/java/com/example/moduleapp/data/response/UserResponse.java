package com.example.moduleapp.data.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String avatar;
    private String createdAt;
}
