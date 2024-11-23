package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.UserUpdateRequest;
import com.example.moduleapp.data.response.UserResponse;
import com.example.moduleapp.service.UserService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse<UserResponse>> getUser() {
        return userService.getProfile().map(ApiResponse::success);
    }

    @PatchMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse<String>> updateProfile(@ModelAttribute UserUpdateRequest userUpdateRequest) {
        return userService.updateProfile(userUpdateRequest).map(ApiResponse::success);
    }


}
