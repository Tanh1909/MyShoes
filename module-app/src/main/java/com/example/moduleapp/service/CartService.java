package com.example.moduleapp.service;


import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.CartRequest;
import com.example.moduleapp.model.tables.pojos.Cart;
import io.reactivex.rxjava3.core.Single;

public interface CartService {
    PageResponse<Cart> findByUser(PageRequest pageRequest);

    Single<String> addToCart(CartRequest cartRequest);
}
