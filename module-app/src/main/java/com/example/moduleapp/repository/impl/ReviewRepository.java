package com.example.moduleapp.repository.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.moduleapp.repository.IRxReviewRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.REVIEW;

@Repository
@RequiredArgsConstructor
public class ReviewRepository extends JooqRepository<Review, Integer> implements IRxReviewRepository {
    private final DSLContext dslContext;

    @Override
    protected Table getTable() {
        return REVIEW;
    }

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    public Single<PageResponse<Review>> getReviewByUserId(PageRequest pageRequest, Integer userId, boolean isReview) {
        int totalRecords = getTotalRecords();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int totalPage = (int) Math.ceil(totalRecords * 1f / size);
        int offset = page * size;
        Byte b = Byte.valueOf(isReview ? "1" : "0");
        Long userIdLong = Long.valueOf(userId);
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(REVIEW.IS_REVIEW.eq(b).and(REVIEW.USER_ID.eq(userIdLong)))
                .offset(offset)
                .limit(size)
                .fetchInto(pojoClass)
        ).map(result -> PageResponse.<Review>builder().data(result).page(page).size(size).totalPage(totalPage).build());
    }

    @Override
    public Single<PageResponse<Review>> getReviewByProductId(PageRequest pageRequest, Integer productId, boolean isReview) {
        int totalRecords = getTotalRecords();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int totalPage = (int) Math.ceil(totalRecords * 1f / size);
        int offset = page * size;
        Byte b = Byte.valueOf(isReview ? "1" : "0");
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(REVIEW.IS_REVIEW.eq(b).and(REVIEW.PRODUCT_ID.eq(productId)))
                .offset(offset)
                .limit(size)
                .fetchInto(pojoClass)
        ).map(result -> PageResponse.<Review>builder().data(result).page(page).size(size).totalPage(totalPage).build());
    }

    @Override
    public Single<Map<Integer, BigDecimal>> getRatedByProductIdIn(Collection<Integer> productIds) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(REVIEW.PRODUCT_ID, DSL.avg(REVIEW.RATING).as("RATING"))
                .from(getTable())
                .where(REVIEW.PRODUCT_ID.in(productIds).and(REVIEW.IS_REVIEW.isTrue()))
                .groupBy(REVIEW.PRODUCT_ID)
                .fetchMap(REVIEW.PRODUCT_ID, DSL.field("RATING", BigDecimal.class))
        );
    }

    @Override
    public Single<Optional<BigDecimal>> getRatedByProductId(Integer productId) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(DSL.avg(REVIEW.RATING).as("RATING"))
                .from(getTable())
                .where(REVIEW.PRODUCT_ID.eq(productId).and(REVIEW.IS_REVIEW.isTrue()))
                .fetchOptional(DSL.field("RATING", BigDecimal.class))
        );
    }
}
