package com.example.moduleapp;

import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.payment.factory.PaymentFactory;
import com.example.security.config.service.UserDetailImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.example")
@Slf4j
public class ModuleAppApplication {
    @Value("${app.module-auth}")
    private String moduleAuth;
    @Autowired
    private PaymentFactory paymentFactory;


    public static void main(String[] args) {
        SpringApplication.run(ModuleAppApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {

        };
    }

}
