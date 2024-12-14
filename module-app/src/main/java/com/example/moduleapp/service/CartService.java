package com.example.moduleapp.service;


import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.CartRequest;
import com.example.moduleapp.data.request.CartUpdateRequest;
import com.example.moduleapp.data.response.CartResponse;
import io.reactivex.rxjava3.core.Single;

public interface CartService {
    Single<PageResponse<CartResponse>> findByUser(PageRequest pageRequest);

    Single<String> addToCart(Integer productVariantId, CartRequest cartRequest);

    Single<String> deleteFromCart(Integer cartId);

    Single<String> updateCart(Integer cartId, CartUpdateRequest cartUpdateRequest);

}
