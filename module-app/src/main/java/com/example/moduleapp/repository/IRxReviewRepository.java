package com.example.moduleapp.repository;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface IRxReviewRepository extends IRxJooqRepository<Review, Integer> {
    Single<PageResponse<Review>> getReviewByUserId(PageRequest pageRequest, Integer userId, boolean isReview);

    Single<PageResponse<Review>> getReviewByProductId(PageRequest pageRequest, Integer productId, boolean isReview);

    Single<Map<Integer, BigDecimal>> getRatedByProductIdIn(Collection<Integer> productIds);

    Single<Optional<BigDecimal>> getRatedByProductId(Integer productId);
}
