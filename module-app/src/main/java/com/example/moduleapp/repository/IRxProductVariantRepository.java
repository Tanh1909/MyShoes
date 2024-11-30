package com.example.moduleapp.repository;

import com.example.moduleapp.data.dto.ProductVariantAttribute;
import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IRxProductVariantRepository extends IRxJooqRepository<ProductVariant, Integer> {

    Single<List<ProductVariant>> findByProductId(Integer productId);

    Single<List<ProductVariant>> findByProductIdIn(Collection<Integer> productIds);

    Single<List<ProductVariant>> insertAndFind(Collection<ProductVariant> productVariants);

    Single<List<ProductVariant>> findByNameIn(Collection<String> skuCodes);

    Single<List<ProductVariantDetail>> findDetailByProductIdsIn(Collection<Integer> ids);

    Single<List<ProductVariantDetail>> findDetailByIdsIn(Collection<Integer> ids);

    Single<List<ProductVariantAttribute>> findAttributeById(Integer id);

    Single<List<ProductVariantAttribute>> findAttributeByIdIn(Collection<Integer> ids);

}
