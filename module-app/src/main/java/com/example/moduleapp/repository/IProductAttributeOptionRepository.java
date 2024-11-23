package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductAttributeOption;
import com.example.repository.IBlockingRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IProductAttributeOptionRepository extends IBlockingRepository<ProductAttributeOption, Integer> {

List<ProductAttributeOption> insertAndFindBlocking(Collection<ProductAttributeOption> productAttributeOptions, Collection<Integer> attributeIds);


List<ProductAttributeOption> findByValueInAndAttributeIdInBlocking(Collection<String> values, Collection<Integer> attrIds);

}
