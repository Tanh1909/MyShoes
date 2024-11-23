package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Category;
import com.example.moduleapp.repository.IRxCategoryRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.CATEGORY;

@Repository
@RequiredArgsConstructor
public class CategoryRepository extends JooqRepository<Category, Integer> implements IRxCategoryRepository {
    private final DSLContext dslContext;

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    protected Table getTable() {
        return CATEGORY;
    }
}
