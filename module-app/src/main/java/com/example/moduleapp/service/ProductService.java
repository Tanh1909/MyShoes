package com.example.moduleapp.service;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.data.response.ProductResponse;
import com.example.moduleapp.model.tables.pojos.Product;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface ProductService {
    Single<String> create(ProductRequest productRequest);
    Single<String> update(Long id,ProductRequest productRequest);
    Single<String> delete(Long id);
    Single<List<ProductResponse>> findAll(PageRequest pageRequest);
    Single<String> addToCart(Long id);
}
