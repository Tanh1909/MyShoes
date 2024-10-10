package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductAttributeOption;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IRxProductAttributeOption extends IRxJooqRepository<ProductAttributeOption, Integer> {

    Single<List<ProductAttributeOption>> findOrInsert(Collection<ProductAttributeOption> productAttributeOptions);

    Single<List<ProductAttributeOption>> findByValueIn(Collection<String> name);
}