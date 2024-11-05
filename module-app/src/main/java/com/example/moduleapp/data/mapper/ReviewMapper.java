package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.ReviewRequest;
import com.example.moduleapp.data.response.ReviewResponse;
import com.example.moduleapp.model.tables.pojos.Review;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReviewMapper extends ConvertMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toReview(@MappingTarget Review review, ReviewRequest reviewRequest);

    ReviewResponse toReviewResponse(Review review);
}
