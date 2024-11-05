package com.example.moduleapp.repository;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface IRxReviewRepository extends IRxJooqRepository<Review, Integer> {
    Single<PageResponse<Review>> getReviewByUserId(PageRequest pageRequest,Integer userId, boolean isReview);
}
