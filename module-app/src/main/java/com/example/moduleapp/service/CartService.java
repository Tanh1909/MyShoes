package com.example.moduleapp.service;


import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.CartUpdateRequest;
import com.example.moduleapp.data.response.CartResponse;
import io.reactivex.rxjava3.core.Single;

public interface CartService {
    Single<PageResponse<CartResponse>> findByUser(PageRequest pageRequest);

    Single<String> addToCart(Integer productVariantId);

    Single<String> deleteFromCart(Long cartId);

    Single<String> updateCart(Long cartId, CartUpdateRequest cartUpdateRequest);

}
