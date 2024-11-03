package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.ProductAttributeOption;
import com.example.moduleapp.repository.IRxProductAttributeOption;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.PRODUCT_ATTRIBUTE_OPTION;

@Repository
@RequiredArgsConstructor
public class ProductAttributeOptionRepository extends JooqRepository<ProductAttributeOption, Integer> implements IRxProductAttributeOption {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return PRODUCT_ATTRIBUTE_OPTION;
    }


    @Override
    public Single<List<ProductAttributeOption>> insertAndFind(Collection<ProductAttributeOption> productAttributeOptions, Collection<Integer> attributeIds) {
        List<String> values = productAttributeOptions.stream().map(ProductAttributeOption::getValue).toList();
        return insertUpdateOnDuplicateKey(productAttributeOptions)
                .flatMap(integers -> findByValueInAndAttributeIdIn(values, attributeIds));
    }

    @Override
    public Single<List<ProductAttributeOption>> findByValueInAndAttributeIdIn(Collection<String> values, Collection<Integer> attrIds) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(PRODUCT_ATTRIBUTE_OPTION)
                .where(PRODUCT_ATTRIBUTE_OPTION.PRODUCT_ATTRIBUTE_ID.in(attrIds)
                        .and(PRODUCT_ATTRIBUTE_OPTION.VALUE.in(values))
                )
                .fetchInto(pojoClass)
        );
    }
}
