package com.example.moduleapp.service;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.data.response.ProductDetailResponse;
import com.example.moduleapp.data.response.ProductResponse;
import io.reactivex.rxjava3.core.Single;

public interface ProductService {
    Single<String> create(ProductRequest productRequest);

    Single<String> update(Integer id, ProductRequest productRequest);

    Single<String> delete(Integer id);

    Single<PageResponse<ProductResponse>> findAll(PageRequest pageRequest);

    Single<PageResponse<ProductResponse>> findByNameLike(String name,PageRequest pageRequest);

    Single<PageResponse<ProductResponse>> findByCategoryId(Integer categoryId, PageRequest pageRequest);

    Single<ProductDetailResponse> findDetail(Integer id);

}
