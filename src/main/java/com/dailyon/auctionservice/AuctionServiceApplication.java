package com.dailyon.auctionservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.document.BidHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.TimeZone;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AuctionServiceApplication {
  @Autowired AmazonDynamoDBAsync dynamoDB;

  @Autowired DynamoDBMapper dynamoDBMapper;

  public static void main(String[] args) {
    SpringApplication.run(AuctionServiceApplication.class, args);
  }

  @PostConstruct
  public void setTimezoneToSeoul() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
  }

  // TODO : document FIX 후 삭제
  @PostConstruct
  @Profile({"!test"})
  public void setDynamoDB() {

    CreateTableRequest createTableRequest =
        dynamoDBMapper
            .generateCreateTableRequest(Auction.class)
            .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

    CreateTableRequest createTableRequest2 =
        dynamoDBMapper
            .generateCreateTableRequest(BidHistory.class)
            .withProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L));

    createTableRequest2
        .getGlobalSecondaryIndexes()
        .forEach(
            idx ->
                idx.withProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L))
                    .withProjection(new Projection().withProjectionType("ALL")));
    TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
    TableUtils.createTableIfNotExists(dynamoDB, createTableRequest2);
  }

  @PreDestroy
  public void deleteDB() {
    TableUtils.deleteTableIfExists(
        dynamoDB, dynamoDBMapper.generateDeleteTableRequest(Auction.class));

    TableUtils.deleteTableIfExists(
        dynamoDB, dynamoDBMapper.generateDeleteTableRequest(BidHistory.class));
  }
}
