package org.example.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.example.common.template.RxTemplate.rxSchedulerIo;


@SpringBootApplication
@Slf4j
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    ApplicationRunner init() {
        return args -> {
           rxSchedulerIo(() -> {
               return "meme";
           }).subscribe((t, throwable) -> {
               System.out.println("meme");
           });
        };
    }
}
