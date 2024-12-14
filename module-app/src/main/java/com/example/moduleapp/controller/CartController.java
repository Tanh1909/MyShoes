package com.example.moduleapp.controller;

import com.example.common.annotation.Pageable;
import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.ApiResponse;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.CartRequest;
import com.example.moduleapp.data.request.CartUpdateRequest;
import com.example.moduleapp.data.response.CartResponse;
import com.example.moduleapp.service.CartService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/{productVariantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse<String>> create(@PathVariable Integer productVariantId, @RequestBody CartRequest cartRequest) {
        return cartService.addToCart(productVariantId,cartRequest).map(ApiResponse::success);
    }

    @GetMapping
    public Single<ApiResponse<PageResponse<CartResponse>>> findByUser(@Pageable PageRequest pageRequest) {
        return cartService.findByUser(pageRequest).map(ApiResponse::success);
    }

    @PatchMapping("/{cartId}")
    public Single<ApiResponse<String>> updateCart(@PathVariable Integer cartId, @RequestBody CartUpdateRequest cartUpdateRequest) {
        return cartService.updateCart(cartId, cartUpdateRequest).map(ApiResponse::success);
    }

    @DeleteMapping("/{cartId}")
    public Single<ApiResponse<String>> deleteCart(@PathVariable Integer cartId) {
        return cartService.deleteFromCart(cartId).map(ApiResponse::success);
    }
}
