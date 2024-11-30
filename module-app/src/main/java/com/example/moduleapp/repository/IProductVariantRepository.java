package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.repository.IBlockingRepository;

import java.util.Collection;
import java.util.List;

public interface IProductVariantRepository extends IBlockingRepository<ProductVariant, Integer> {
    List<ProductVariant> insertAndFindBlocking(Collection<ProductVariant> productVariants, Integer productId);

    List<ProductVariant> findByProductIdBlocking(Integer productId);

}
