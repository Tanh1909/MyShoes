package com.example.common.template;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Supplier;

@Slf4j
public class RxTemplate {
    public static <T> Single<T> rxSchedulerIo(Supplier<T> supplier) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Single.<T>create(emitter -> {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("[THREAD] {}, supplier: {}", Thread.currentThread().getName(), supplier.getClass().getSimpleName());
                    emitter.onSuccess(supplier.get());
                })
                .subscribeOn(Schedulers.io());
    }

}
