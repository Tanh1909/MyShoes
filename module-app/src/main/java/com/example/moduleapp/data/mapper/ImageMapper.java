package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.ImageCreationRequest;
import com.example.moduleapp.data.response.ImageResponse;
import com.example.moduleapp.model.tables.pojos.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    Image toImage(ImageCreationRequest imageCreationRequest);

    ImageResponse toImageResponse(Image image);

    default Byte booleanToByte(boolean value) {
        if (value) return 1;
        return 0;
    }
    default boolean byteToBoolean(Byte value) {
        return value == 1;
    }
}
