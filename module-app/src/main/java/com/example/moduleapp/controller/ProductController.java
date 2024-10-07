package com.example.moduleapp.controller;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.service.ProductService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse> create(@ModelAttribute ProductRequest productRequest) {
        return productService.create(productRequest).map(ApiResponse::success);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> findAll(@ModelAttribute PageRequest pageRequest) {
        return productService.findAll(pageRequest).map(ApiResponse::success);
    }
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> update(@PathVariable Long id,@ModelAttribute ProductRequest productRequest) {
        return productService.update(id,productRequest).map(ApiResponse::success);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> deleteById(@PathVariable Long id) {
        return productService.delete(id).map(ApiResponse::success);
    }

    @PostMapping("/{id}/cart")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> addToCart() {
        return productService.addToCart(1l).map(ApiResponse::success);
    }

}
