package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductAttributeOption;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IRxProductAttributeOption extends IRxJooqRepository<ProductAttributeOption, Integer> {


    Single<List<ProductAttributeOption>> insertAndFind(Collection<ProductAttributeOption> productAttributeOptions, Collection<Integer> attributeIds);


    Single<List<ProductAttributeOption>> findByValueInAndAttributeIdIn(Collection<String> values, Collection<Integer> attrIds);
}
