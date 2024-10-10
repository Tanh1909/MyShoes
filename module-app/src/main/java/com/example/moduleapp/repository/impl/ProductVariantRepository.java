package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.repository.JooqRepository;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.PRODUCT_VARIANT;

@Repository
@AllArgsConstructor
public class ProductVariantRepository extends JooqRepository<ProductVariant, Integer> {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return PRODUCT_VARIANT;
    }
}
