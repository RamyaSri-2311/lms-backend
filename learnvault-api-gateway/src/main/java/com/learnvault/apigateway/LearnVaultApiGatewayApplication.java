package com.learnvault.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LearnVaultApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnVaultApiGatewayApplication.class, args);
    }
}

