package com.example.moduleapp.controller;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.service.ProductService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
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

    public static InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse> create(@RequestBody ProductRequest productRequest) {
        return productService.create(productRequest).map(ApiResponse::success);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> findAll(@ModelAttribute PageRequest pageRequest) {
        return productService.findAll(pageRequest).map(ApiResponse::success);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> update(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
        return productService.update(id, productRequest).map(ApiResponse::success);
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

    @GetMapping("/test")
    public Single<String> test() {
        System.out.println(Thread.currentThread().getName());
        threadLocal.set("test: " + Thread.currentThread().getName());
        return Single.fromCallable(() -> {
                    System.out.println("thread executing: " + Thread.currentThread().getName());
                    System.out.println(this.threadLocal.get());
//                    Thread.sleep(1000);
                    return "success";
                })
                .subscribeOn(Schedulers.io());
//                .doOnSubscribe(disposable -> System.out.println("thread when subscribe:" + Thread.currentThread().getName()))
//                .doAfterSuccess(disposable -> System.out.println("thread after success:" + Thread.currentThread().getName()))
//                .doOnTerminate(() -> System.out.println("thread after terminate:" + Thread.currentThread().getName()));
    }

    @GetMapping("/testThread")
    public Single<String> testThread() {
        System.out.println(Thread.currentThread().getName());
        System.out.println(this.threadLocal.get());
        return Single.fromCallable(() -> {
//                    System.out.println(Thread.currentThread().getName());
                    System.out.println(this.threadLocal.get());
                    System.out.println("thread executing: " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                    return "success";
                })
                .subscribeOn(Schedulers.io());
//                .doOnSubscribe(disposable -> System.out.println("thread when subscribe:" + Thread.currentThread().getName()))
//                .doAfterSuccess(disposable -> System.out.println("thread after success:" + Thread.currentThread().getName()))
//                .doOnTerminate(() -> System.out.println("thread after terminate:" + Thread.currentThread().getName()));
    }

}
