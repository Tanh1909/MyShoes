package com.example.common.template;

import com.example.common.context.SecurityContext;
import com.example.common.context.SimpleSecurityUser;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RxTemplate {
    public static <T> Single<T> rxSchedulerIo(Supplier<T> supplier) {
        SimpleSecurityUser simpleSecurityUser = SecurityContext.getSimpleSecurityUser();
        return Single.<T>create(emitter -> {
                    SecurityContext.setSimpleSecurityUser(simpleSecurityUser);
                    log.info("[THREAD] {}, supplier: {}", Thread.currentThread().getName(), supplier.getClass().getSimpleName());
                    emitter.onSuccess(supplier.get());
                })
                .subscribeOn(Schedulers.io());
    }

}
