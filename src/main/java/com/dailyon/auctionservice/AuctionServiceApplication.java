package com.dailyon.auctionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AuctionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServiceApplication.class, args);
    }

}
