package com.example.security.controller;

import com.example.common.template.RxTemplate;
import io.reactivex.rxjava3.core.Single;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public Single<Authentication> test() {
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        return RxTemplate.rxSchedulerIo(
                () -> {
                    SecurityContextHolder.getContext().setAuthentication(authentication1);
                    System.out.println(SecurityContextHolder.getContext());
                    return authentication1;
                });

    }
}
