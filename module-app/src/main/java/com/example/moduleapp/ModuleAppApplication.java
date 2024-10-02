package com.example.moduleapp;

import com.example.security.config.WebSecurityConfigAdapter;
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
    private WebSecurityConfigAdapter webSecurityConfigAdapter;


    public static void main(String[] args) {
        SpringApplication.run(ModuleAppApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("STARTING MODULE APP!");
            log.info(moduleAuth);
        };
    }

}
