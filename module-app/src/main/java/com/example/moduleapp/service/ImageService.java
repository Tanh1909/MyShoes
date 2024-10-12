package com.example.moduleapp.service;

import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.response.ImageResponse;
import io.reactivex.rxjava3.core.Single;

public interface ImageService {
    Single<ImageResponse> create(ImageCreationRequest request);
}
