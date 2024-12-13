package com.example.moduleapp.repository;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Product;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.Map;

public interface IRxProductRepository extends IRxJooqRepository<Product, Integer> {
    Single<Map<Integer, Integer>> getNumberOfPaid(Collection<Integer> productIds);

    Single<Integer> getNumberOfPaid(Integer productId);

    Single<PageResponse<Product>> findByCategoryId(Integer categoryId, PageRequest pageRequest);

    Single<PageResponse<Product>> findByNameLike(String productName, PageRequest pageRequest);
}
