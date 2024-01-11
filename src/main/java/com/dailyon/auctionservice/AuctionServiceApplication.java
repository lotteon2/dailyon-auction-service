package com.dailyon.auctionservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dailyon.auctionservice.document.Auction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.TimeZone;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AuctionServiceApplication {
    @Autowired
    AmazonDynamoDBAsync dynamoDB;

    @Autowired
    DynamoDBMapper dynamoDBMapper;

    public static void main(String[] args) {
        SpringApplication.run(AuctionServiceApplication.class, args);
    }

    @PostConstruct
    public void setTimezoneToSeoul() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    // TODO : document FIX 후 삭제
    @PostConstruct
    public void setDynamoDB() {
        CreateTableRequest createTableRequest = dynamoDBMapper
                .generateCreateTableRequest(Auction.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
    }

    @PreDestroy
    public void deleteDynamoDB() {
        TableUtils.deleteTableIfExists(dynamoDB, dynamoDBMapper.generateDeleteTableRequest(Auction.class));
    }
}
