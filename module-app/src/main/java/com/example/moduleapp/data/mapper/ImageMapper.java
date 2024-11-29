package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.response.ImageResponse;
import com.example.moduleapp.model.tables.pojos.Image;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper extends ConvertMapper {
    Image toImage(ImageCreationRequest imageCreationRequest);

    ImageResponse toImageResponse(Image image);

    List<ImageResponse> toImageResponses(List<Image> images);

}
