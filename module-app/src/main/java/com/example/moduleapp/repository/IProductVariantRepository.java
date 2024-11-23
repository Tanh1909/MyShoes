package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.repository.IBlockingRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IProductVariantRepository extends IBlockingRepository<ProductVariant, Integer> {
   List<ProductVariant> insertAndFindBlocking(Collection<ProductVariant> productVariants);

   List<ProductVariant> findByNameInBlocking(Collection<String> skuCodes);
}
