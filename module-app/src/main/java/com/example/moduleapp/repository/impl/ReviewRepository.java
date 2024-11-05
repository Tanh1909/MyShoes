package com.example.moduleapp.repository.impl;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.moduleapp.repository.IRxReviewRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

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
}
