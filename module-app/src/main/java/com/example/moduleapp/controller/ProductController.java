package com.example.moduleapp.controller;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.service.ProductService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Log4j2
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse> create(@RequestBody ProductRequest productRequest) {
        return productService.create(productRequest).map(ApiResponse::success);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> findAll(PageRequest pageRequest) {
        return productService.findAll(pageRequest).map(ApiResponse::success);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> update(@PathVariable Integer id, ProductRequest productRequest) {
        return productService.update(id, productRequest).map(ApiResponse::success);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> deleteById(@PathVariable Integer id) {
        return productService.delete(id).map(ApiResponse::success);
    }

    @PostMapping("/{id}/cart")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> addToCart() {
        return productService.addToCart(1).map(ApiResponse::success);
    }


}
