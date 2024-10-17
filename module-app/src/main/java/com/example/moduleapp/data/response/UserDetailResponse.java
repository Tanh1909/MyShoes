package com.example.moduleapp.data.response;

import com.example.common.data.response.base.BaseResponse;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
public class UserDetailResponse extends BaseResponse {
    private String id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private List<String> roles;
}