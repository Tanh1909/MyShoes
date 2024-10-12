package com.example.moduleapp.service.impl;

import com.example.cloudinary.service.IUploadFileService;
import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;
import com.example.moduleapp.data.mapper.ImageMapper;
import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.response.ImageResponse;
import com.example.moduleapp.model.tables.pojos.Image;
import com.example.moduleapp.repository.impl.ImageRepository;
import com.example.moduleapp.service.ImageService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final IUploadFileService uploadFileService;
    private final ImageMapper imageMapper;

    @Override
    public Single<ImageResponse> create(ImageCreationRequest imageCreationRequest) {
        if (imageCreationRequest.getImage() == null) {
            throw new AppException(ErrorCodeBase.IS_NULL, "IMAGE");
        }
        return uploadFileService.rxUpload(imageCreationRequest.getImage())
                .flatMap(url -> {
                    Image image = imageMapper.toImage(imageCreationRequest);
                    image.setUrl(url);
                    return imageRepository.insertReturn(image);
                }).map(imageMapper::toImageResponse);
    }
}
