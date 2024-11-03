package com.example.moduleapp;

import com.example.moduleapp.repository.impl.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

@Slf4j
@EnableKafka
@SpringBootApplication(scanBasePackages = "com.example")
@RequiredArgsConstructor
public class ModuleAppApplication {
    private final ProductRepository productRepository;

    public static void main(String[] args) {
        SpringApplication.run(ModuleAppApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {

        };
    }

}
