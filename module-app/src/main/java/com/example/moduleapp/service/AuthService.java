package com.example.moduleapp.service;

import com.example.common.context.UserPrincipal;
import com.example.moduleapp.data.request.AuthRequest;
import com.example.moduleapp.data.request.UserCreationRequest;
import com.example.moduleapp.data.response.AuthResponse;
import com.example.moduleapp.data.response.UserResponse;
import io.reactivex.rxjava3.core.Single;

public interface AuthService {
    Single<AuthResponse> login(AuthRequest authRequest);

    Single<AuthResponse> refreshToken(String refreshToken);

    Single<UserResponse> signUp(UserCreationRequest userCreationRequest);

    UserPrincipal getCurrentUser();

    void validateOwner(Long userId);

}
