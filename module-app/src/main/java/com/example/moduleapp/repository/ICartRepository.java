package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.repository.IBlockingRepository;

import java.util.Collection;
import java.util.List;

public interface ICartRepository extends IBlockingRepository<Cart,Integer> {
    List<Cart> findByUserIdAndProductVariantIdInBlocking(long userId, Collection<Integer> productVariantIds);
}
