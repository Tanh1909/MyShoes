package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductAttribute;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IRxProductAttributeRepository extends IRxJooqRepository<ProductAttribute, Integer> {
    Single<List<ProductAttribute>> insertAndFind(Collection<ProductAttribute> productAttributes);

    Single<List<ProductAttribute>> findByNameIn(Collection<String> names);
}
