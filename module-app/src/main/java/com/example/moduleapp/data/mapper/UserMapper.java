package com.example.moduleapp.data.mapper;

import com.example.common.context.SimpleSecurityUser;
import com.example.moduleapp.data.request.UserCreationRequest;
import com.example.moduleapp.data.response.UserDetailResponse;
import com.example.moduleapp.data.response.UserResponse;
import com.example.moduleapp.model.tables.pojos.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDetailResponse toUserDetailResponse(User user);

    User toUser(UserCreationRequest userCreationRequest);

    UserResponse toUserResponse(User user);

    SimpleSecurityUser toSimpleSecurityUser(UserDetailResponse userDetailResponse);
}
