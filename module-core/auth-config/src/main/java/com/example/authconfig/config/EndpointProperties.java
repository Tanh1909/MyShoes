package com.example.authconfig.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "auth")
public class EndpointProperties {
    private List<String> publicEndPoints;

    @Bean("publicEndpoints")
    public List<String> publicEndpoint() {
        return publicEndPoints;
    }
}
