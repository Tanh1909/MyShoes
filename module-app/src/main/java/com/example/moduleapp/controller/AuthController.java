package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.AuthRequest;
import com.example.moduleapp.data.request.UserCreationRequest;
import com.example.moduleapp.data.response.AuthResponse;
import com.example.moduleapp.data.response.UserResponse;
import com.example.moduleapp.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse<UserResponse>> signup(@RequestBody UserCreationRequest userCreationRequest) {
        return authService.signUp(userCreationRequest).map(ApiResponse::success);
    }


    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest).map(ApiResponse::success);
    }

}