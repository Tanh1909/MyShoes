package com.example.security;

import com.example.security.repository.impl.RoleRepository;
import com.example.security.repository.impl.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class Security {
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    public static void main(String[] args) {
        SpringApplication.run(Security.class, args);
    }
    @Bean
    public ApplicationRunner initSecurity(){
        return args -> {

        };
    }
}
