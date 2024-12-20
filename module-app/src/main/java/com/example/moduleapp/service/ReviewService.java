package com.example.moduleapp.service;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.ReviewRequest;
import com.example.moduleapp.data.response.ReviewResponse;
import io.reactivex.rxjava3.core.Single;

public interface ReviewService {
    Single<PageResponse<ReviewResponse>> findReview(PageRequest pageRequest, boolean isReview);

    Single<PageResponse<ReviewResponse>> findReviewByProductId(PageRequest pageRequest, Integer productId);

    Single<Boolean> review(Integer reviewId, ReviewRequest reviewRequest);
}
