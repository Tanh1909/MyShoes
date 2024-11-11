package com.example.moduleapp.service;

import com.example.moduleapp.data.dto.ProductVariantDetail;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface ProductVariantService {
    Single<ProductVariantDetail> findDetailById(Integer id);

    Single<List<ProductVariantDetail>> findDetailsByIdIn(Collection<Integer> ids);

    Single<List<ProductVariantDetail>> findDetailsByProductId(Integer productId);
}
