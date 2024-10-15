package com.example.authconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/test")
public class AuthConfigApplication {
    @GetMapping
    public String index() {
        return "index";
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthConfigApplication.class, args);
    }

}
