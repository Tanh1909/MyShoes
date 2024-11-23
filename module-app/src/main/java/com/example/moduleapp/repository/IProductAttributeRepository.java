package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductAttribute;
import com.example.repository.IBlockingRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IProductAttributeRepository extends IBlockingRepository<ProductAttribute, Integer> {
    List<ProductAttribute> insertAndFindBlocking(Collection<ProductAttribute> productAttributes);
    List<ProductAttribute> findByNameInBlocking(Collection<String> names);
}
