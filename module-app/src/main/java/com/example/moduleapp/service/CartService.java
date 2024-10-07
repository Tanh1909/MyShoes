package com.example.moduleapp.service;


import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Cart;

public interface CartService {
     PageResponse<Cart> findAll(PageRequest pageRequest);
}
