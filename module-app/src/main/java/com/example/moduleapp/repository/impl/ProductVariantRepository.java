package com.example.moduleapp.repository.impl;

import com.example.moduleapp.data.dto.ProductVariantAttribute;
import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.repository.IProductVariantRepository;
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
import static com.example.moduleapp.model.Tables.*;

@Repository
@AllArgsConstructor
public class ProductVariantRepository extends JooqRepository<ProductVariant, Integer>
        implements IRxProductVariantRepository, IProductVariantRepository {
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
    public Single<List<ProductVariant>> findByProductId(Integer productId) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(PRODUCT_VARIANT.PRODUCT_ID.eq(productId))
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

    @Override
    public Single<List<ProductVariantDetail>> findDetailByProductIdsIn(Collection<Integer> ids) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(
                        PRODUCT_VARIANT.ID, PRODUCT_VARIANT.PRODUCT_ID, PRODUCT_VARIANT.PRICE, PRODUCT_VARIANT.SKU_CODE,
                        PRODUCT_ATTRIBUTE_OPTION.VALUE
                )
                .from(getTable())
                .join(PRODUCT_VARIANTS_ATTRIBUTE_OPTION).on(PRODUCT_VARIANT.ID.eq(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.VARIANT_ID))
                .join(PRODUCT_ATTRIBUTE_OPTION).on(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.PRODUCT_ATTRIBUTE_OPTION_ID.eq(PRODUCT_ATTRIBUTE_OPTION.ID))
                .where(PRODUCT_VARIANT.PRODUCT_ID.in(ids))
                .fetchInto(ProductVariantDetail.class)
        );
    }

    @Override
    public Single<List<ProductVariantDetail>> findDetailByIdsIn(Collection<Integer> ids) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(
                        PRODUCT_VARIANT.ID, PRODUCT_VARIANT.PRODUCT_ID, PRODUCT_VARIANT.PRICE, PRODUCT_VARIANT.SKU_CODE,
                        PRODUCT_ATTRIBUTE_OPTION.VALUE
                )
                .from(getTable())
                .join(PRODUCT_VARIANTS_ATTRIBUTE_OPTION).on(PRODUCT_VARIANT.ID.eq(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.VARIANT_ID))
                .join(PRODUCT_ATTRIBUTE_OPTION).on(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.PRODUCT_ATTRIBUTE_OPTION_ID.eq(PRODUCT_ATTRIBUTE_OPTION.ID))
                .where(PRODUCT_VARIANT.ID.in(ids))
                .fetchInto(ProductVariantDetail.class)
        );
    }

    @Override
    public Single<List<ProductVariantAttribute>> findAttributeById(Integer id) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(
                        PRODUCT_VARIANTS_ATTRIBUTE_OPTION.VARIANT_ID,
                        PRODUCT_ATTRIBUTE.NAME,
                        PRODUCT_ATTRIBUTE_OPTION.VALUE
                )
                .from(PRODUCT_ATTRIBUTE)
                .join(PRODUCT_ATTRIBUTE_OPTION).on(PRODUCT_ATTRIBUTE.ID.eq(PRODUCT_ATTRIBUTE_OPTION.PRODUCT_ATTRIBUTE_ID))
                .join(PRODUCT_VARIANTS_ATTRIBUTE_OPTION).on(PRODUCT_ATTRIBUTE_OPTION.ID.eq(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.PRODUCT_ATTRIBUTE_OPTION_ID))
                .where(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.VARIANT_ID.eq(id))
                .fetchInto(ProductVariantAttribute.class)
        );
    }

    @Override
    public Single<List<ProductVariantAttribute>> findAttributeByIdIn(Collection<Integer> ids) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(
                        PRODUCT_VARIANTS_ATTRIBUTE_OPTION.VARIANT_ID,
                        PRODUCT_ATTRIBUTE.NAME,
                        PRODUCT_ATTRIBUTE_OPTION.VALUE
                )
                .from(PRODUCT_ATTRIBUTE)
                .join(PRODUCT_ATTRIBUTE_OPTION).on(PRODUCT_ATTRIBUTE.ID.eq(PRODUCT_ATTRIBUTE_OPTION.PRODUCT_ATTRIBUTE_ID))
                .join(PRODUCT_VARIANTS_ATTRIBUTE_OPTION).on(PRODUCT_ATTRIBUTE_OPTION.ID.eq(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.PRODUCT_ATTRIBUTE_OPTION_ID))
                .where(PRODUCT_VARIANTS_ATTRIBUTE_OPTION.VARIANT_ID.in(ids))
                .fetchInto(ProductVariantAttribute.class)
        );
    }

    @Override
    public List<ProductVariant> insertAndFindBlocking(Collection<ProductVariant> productVariants) {
        List<String> skuCodes = productVariants.stream().map(ProductVariant::getSkuCode).toList();
        insertBlocking(productVariants);
        return findByNameInBlocking(skuCodes);
    }

    @Override
    public List<ProductVariant> findByNameInBlocking(Collection<String> skuCodes) {
        return getDSLContext()
                .select()
                .from(getTable())
                .where(PRODUCT_VARIANT.SKU_CODE.in(skuCodes))
                .fetchInto(pojoClass);
    }
}
