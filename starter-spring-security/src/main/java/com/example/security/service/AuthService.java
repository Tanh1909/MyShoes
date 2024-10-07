package com.example.security.service;

import com.example.security.data.request.AuthRequest;
import com.example.security.data.request.UserCreationRequest;
import com.example.security.data.response.AuthResponse;
import com.example.security.data.response.UserResponse;
import com.example.security.model.tables.pojos.User;
import io.reactivex.rxjava3.core.Single;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    Single<AuthResponse> login(AuthRequest authRequest);

    Single<AuthResponse> refreshToken(String refreshToken);

    Single<UserResponse> signUp(UserCreationRequest userCreationRequest);

    Single<String> logout(String token);

    UserDetails getCurrentUser();

}
