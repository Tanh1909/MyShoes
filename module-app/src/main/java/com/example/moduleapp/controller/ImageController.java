package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.response.ImageResponse;
import com.example.moduleapp.service.ImageService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public Single<ApiResponse<ImageResponse>> create(@ModelAttribute ImageCreationRequest imageCreationRequest) {
        return imageService.create(imageCreationRequest).map(ApiResponse::success);
    }
}
