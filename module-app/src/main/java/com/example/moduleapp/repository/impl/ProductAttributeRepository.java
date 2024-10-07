package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.ProductAttribute;
import com.example.moduleapp.repository.IRxProductAttributeRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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
    public Single<List<ProductAttribute>> findOrInsert(Collection<ProductAttribute> productAttributeReqs) {
        Set<String> nameReqs = productAttributeReqs.stream().map(ProductAttribute::getName).collect(Collectors.toSet());
        return this.findByNameIn(nameReqs)
                .flatMap(pas -> {
                    Set<String> name = pas.stream().map(ProductAttribute::getName).collect(Collectors.toSet());
                    Collection<ProductAttribute> productMissing = productAttributeReqs.stream()
                            .filter(productAttribute -> !name.contains(productAttribute.getName()))
                            .toList();
                    if (!CollectionUtils.isEmpty(productMissing)) {
                        return insertReturn(productMissing)
                                .flatMap(productAttributes -> {
                                    pas.addAll(productAttributes);
                                    return Single.just(pas);
                                });
                    }
                    return Single.just(pas);
                });
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
