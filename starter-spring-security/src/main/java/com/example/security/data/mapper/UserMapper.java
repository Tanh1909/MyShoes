package com.example.security.data.mapper;

import com.example.security.data.request.UserCreationRequest;
import com.example.security.data.response.UserDetailResponse;
import com.example.security.data.response.UserResponse;
import com.example.security.model.tables.pojos.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDetailResponse toUserDetailResponse(User user);
    User toUser(UserCreationRequest userCreationRequest);
    UserResponse toUserResponse(User user);
}
