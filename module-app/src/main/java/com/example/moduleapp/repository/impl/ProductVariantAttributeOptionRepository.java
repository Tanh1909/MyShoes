package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.Tables;
import com.example.moduleapp.model.tables.pojos.ProductVariantsAttributeOption;
import com.example.moduleapp.repository.IProductVariantAttributeOptionRepository;
import com.example.moduleapp.repository.IRxProductVariantAttributeOptionRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductVariantAttributeOptionRepository extends JooqRepository<ProductVariantsAttributeOption, Integer>
        implements IProductVariantAttributeOptionRepository, IRxProductVariantAttributeOptionRepository {
    private final DSLContext dslContext;

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    protected Table getTable() {
        return Tables.PRODUCT_VARIANTS_ATTRIBUTE_OPTION;
    }
}
