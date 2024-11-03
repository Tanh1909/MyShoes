package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Review;
import com.example.moduleapp.repository.IRxReviewRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

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
}
