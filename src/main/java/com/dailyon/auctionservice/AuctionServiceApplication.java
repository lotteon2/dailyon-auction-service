package com.dailyon.auctionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AuctionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServiceApplication.class, args);
    }

    @PostConstruct
    public void setTimezoneToSeoul() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
