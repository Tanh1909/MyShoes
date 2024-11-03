package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.ProductAttribute;
import com.example.moduleapp.repository.IRxProductAttributeRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.PRODUCT_ATTRIBUTE;

@Repository
@RequiredArgsConstructor
public class ProductAttributeRepository extends JooqRepository<ProductAttribute, Integer> implements IRxProductAttributeRepository {
    private final DSLContext dslContext;

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    protected Table getTable() {
        return PRODUCT_ATTRIBUTE;
    }

    @Override
    public Single<List<ProductAttribute>> insertAndFind(Collection<ProductAttribute> productAttributeReqs) {
        Set<String> nameReqs = productAttributeReqs.stream().map(ProductAttribute::getName).collect(Collectors.toSet());
        return insertUpdateOnDuplicateKey(productAttributeReqs)
                .flatMap(integers -> findByNameIn(nameReqs));
    }

    @Override
    public Single<List<ProductAttribute>> findByNameIn(Collection<String> names) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(PRODUCT_ATTRIBUTE.NAME.in(names))
                .fetchInto(pojoClass)
        );
    }
}
