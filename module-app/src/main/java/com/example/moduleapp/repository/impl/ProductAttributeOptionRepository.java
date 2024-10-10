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
import java.util.Set;
import java.util.stream.Collectors;

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
    public Single<List<ProductAttributeOption>> findOrInsert(Collection<ProductAttributeOption> productAttributeOptions) {
        Set<String> valueReq = productAttributeOptions.stream()
                .map(ProductAttributeOption::getValue)
                .collect(Collectors.toSet());
        return findByValueIn(valueReq)
                .flatMap(paos -> {
                    Set<String> value = paos.stream()
                            .map(ProductAttributeOption::getValue)
                            .collect(Collectors.toSet());
                    Collection<ProductAttributeOption> missingProductAttributeOptions = productAttributeOptions.stream()
                            .filter(productAttributeOption -> !value.contains(productAttributeOption.getValue()))
                            .toList();
                    if (missingProductAttributeOptions.isEmpty()) {
                        return Single.just(paos);
                    }
                    return insertReturn(missingProductAttributeOptions)
                            .flatMap(productAttributeOptions1 -> {
                                paos.addAll(productAttributeOptions1);
                                return Single.just(paos);
                            });

                });
    }

    @Override
    public Single<List<ProductAttributeOption>> findByValueIn(Collection<String> name) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(PRODUCT_ATTRIBUTE_OPTION)
                .where(PRODUCT_ATTRIBUTE_OPTION.VALUE.in(name))
                .fetchInto(pojoClass)
        );
    }
}
