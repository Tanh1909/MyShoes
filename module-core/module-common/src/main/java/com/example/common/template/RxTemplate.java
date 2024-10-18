package com.example.common.template;

import com.example.common.context.SecurityContext;
import com.example.common.context.SimpleSecurityUser;
import com.example.common.context.UserPrincipal;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RxTemplate {
    public static <T> Single<T> rxSchedulerIo(Supplier<T> supplier) {
        UserPrincipal userPrincipal = SecurityContext.getUserPrincipal();
        return Single.<T>create(emitter -> {
                    SecurityContext.setContext(userPrincipal);
                    log.info("[THREAD] {}, supplier: {}", Thread.currentThread().getName(), supplier.getClass().getSimpleName());
                    emitter.onSuccess(supplier.get());
                })
                .subscribeOn(Schedulers.io());
    }

}
