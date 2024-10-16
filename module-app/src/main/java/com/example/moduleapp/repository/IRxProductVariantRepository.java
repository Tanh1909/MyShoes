package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IRxProductVariantRepository extends IRxJooqRepository<ProductVariant, Integer> {
    Single<List<ProductVariant>> findByIdIn(Collection<Integer> ids);
}
