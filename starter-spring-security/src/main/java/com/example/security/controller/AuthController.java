package com.example.security.controller;

import com.example.common.data.response.ApiResponse;
import com.example.security.data.request.AuthRequest;
import com.example.security.data.request.UserCreationRequest;
import com.example.security.data.response.AuthResponse;
import com.example.security.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.module-auth}")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse<AuthResponse>> signup(@RequestBody UserCreationRequest userCreationRequest){
        return authService.signUp(userCreationRequest).map(ApiResponse::success);
    }


    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest){
        return authService.login(authRequest).map(ApiResponse::success);
    }
//    @PostMapping("/logout")
//    @ResponseStatus(HttpStatus.OK)
//    public ApiResponse logout(@RequestParam String token ){
//        authService.logout(token);
//        return ApiResponse.success(null);
//    }
//    @PostMapping("/refresh")
//    @ResponseStatus(HttpStatus.OK)
//    public ApiResponse<AuthResponse> refreshToken(@RequestParam String token){
//        return ApiResponse.success(authService.refreshToken(token));
//    }
}