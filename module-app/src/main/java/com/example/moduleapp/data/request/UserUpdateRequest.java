package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateRequest {
    private String email;
    private String fullName;
    private Integer avatarId;
    private MultipartFile avatar;
    private String password;
}
