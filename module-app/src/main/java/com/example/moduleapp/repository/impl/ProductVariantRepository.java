package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.repository.IRxProductVariantRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.PRODUCT_VARIANT;

@Repository
@AllArgsConstructor
public class ProductVariantRepository extends JooqRepository<ProductVariant, Integer> implements IRxProductVariantRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return PRODUCT_VARIANT;
    }

    @Override
    public Single<List<ProductVariant>> findByIdIn(Collection<Integer> ids) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(PRODUCT_VARIANT.ID.in(ids))
                .fetchInto(pojoClass)
        );
    }

    @Override
    public Single<List<ProductVariant>> insertAndFind(Collection<ProductVariant> productVariants) {
        List<String> skuCodes = productVariants.stream().map(ProductVariant::getSkuCode).toList();
        return insert(productVariants)
                .flatMap(integers -> findByNameIn(skuCodes));
    }

    @Override
    public Single<List<ProductVariant>> findByNameIn(Collection<String> skuCodes) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(PRODUCT_VARIANT.SKU_CODE.in(skuCodes))
                .fetchInto(pojoClass)
        );
    }
}
