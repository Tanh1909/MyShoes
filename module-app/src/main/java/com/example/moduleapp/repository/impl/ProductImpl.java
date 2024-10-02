package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.repository.IRxProductRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.PRODUCT;

@Repository
@RequiredArgsConstructor
public class ProductImpl extends JooqRepository<Product, Long> implements IRxProductRepository {
    private final DSLContext dslContext;

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    protected Table getTable() {
        return PRODUCT;
    }

}
