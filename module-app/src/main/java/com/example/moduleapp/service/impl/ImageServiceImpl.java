package com.example.moduleapp.service.impl;

import com.example.cloudinary.service.IUploadFileService;
import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.mapper.ImageMapper;
import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.request.ImageRequest;
import com.example.moduleapp.data.response.ImageResponse;
import com.example.moduleapp.model.tables.pojos.Image;
import com.example.moduleapp.repository.impl.ImageRepository;
import com.example.moduleapp.service.ImageService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.common.config.constant.CommonConstant.*;

@Service
@RequiredArgsConstructor
@Log4j2
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

    @Override
    public Single<String> chooseDefault(Integer imageId, Integer targetId, ImageEnum imageEnum) {
        return imageRepository.findByTargetIdAndType(targetId, imageEnum.getValue())
                .flatMap(images -> {
                    images.forEach(image -> {
                        image.setIsPrimary(image.getId().equals(imageId) ? TRUE : FALSE);
                    });
                    return imageRepository.insertUpdateOnDuplicateKey(images);
                })
                .map(integers -> SUCCESS);
    }


    @Override
    @Transactional
    public void updateImagesBlocking(List<ImageRequest> imageRequests, Integer targetId, ImageEnum imageEnum) {
        String type = imageEnum.getValue();
        Map<Integer, ImageRequest> mapImageReq = imageRequests.stream()
                .collect(Collectors.toMap(ImageRequest::getId, img -> img));
        List<Image> imagesReq = imageRepository.findByIdsAndTargetIdNullableAndTypeBlocking(mapImageReq.keySet(), targetId, type);
        if (!CollectionUtils.isEmpty(imageRequests)) {
            deleteMissingId(imagesReq, mapImageReq.keySet());
            Integer defaultId = imageRequests.stream()
                    .filter(ImageRequest::getIsPrimary)
                    .findFirst()
                    .orElse(imageRequests.getFirst())
                    .getId();
            Map<Integer, Image> mapImage = imagesReq.stream().collect(Collectors.toMap(
                    Image::getId,
                    o -> o
            ));
            List<Image> insertImage = imageRequests.stream()
                    .map(imageRequest -> {
                        Image image = mapImage.getOrDefault(imageRequest.getId(), null);
                        if (image == null) {
                            throw new AppException(ErrorCodeBase.NOT_FOUND, "IMAGE");
                        }
                        image.setType(type);
                        image.setTargetId(targetId);
                        image.setIsPrimary(image.getId().equals(defaultId) ? TRUE : FALSE);
                        return image;
                    }).toList();
            imageRepository.insertUpdateOnDuplicateKeyBlocking(insertImage);

        }
    }

    private void deleteMissingId(List<Image> images, Set<Integer> imageIds) {
        Set<Integer> imageIdsResult = images.stream().map(Image::getId).collect(Collectors.toSet());
        Set<Integer> imageIdMissing = new HashSet<>();
        imageIdsResult.forEach(id -> {
            if (!imageIds.contains(id)) {
                imageIdMissing.add(id);
            }
        });
        if (!CollectionUtils.isEmpty(imageIdMissing)) {
            imageRepository.deleteByIdsBlocking(imageIdMissing);
        }
    }
}
