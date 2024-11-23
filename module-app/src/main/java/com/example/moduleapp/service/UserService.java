package com.example.moduleapp.service;

import com.example.moduleapp.data.request.UserUpdateRequest;
import com.example.moduleapp.data.response.UserResponse;
import io.reactivex.rxjava3.core.Single;

public interface UserService {
    Single<UserResponse> getProfile();

    Single<String> updateProfile(UserUpdateRequest userUpdateRequest);
}
