package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.response.ImageResponse;
import com.example.moduleapp.model.tables.pojos.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    Image toImage(ImageCreationRequest imageCreationRequest);

    ImageResponse toImageResponse(Image image);

}
