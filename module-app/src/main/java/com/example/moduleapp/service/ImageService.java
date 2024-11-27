package com.example.moduleapp.service;

import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.request.ImageRequest;
import com.example.moduleapp.data.response.ImageResponse;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface ImageService {
    Single<ImageResponse> create(ImageCreationRequest request);

    Single<String> chooseDefault(Integer imageId, Integer targetId, ImageEnum imageEnum);

    void updateImagesBlocking(List<ImageRequest> imageRequests, Integer targetId, ImageEnum imageEnum);

}
