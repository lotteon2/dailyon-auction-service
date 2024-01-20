package com.dailyon.auctionservice.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.document.AuctionHistory;
import com.dailyon.auctionservice.document.BidHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@Profile({"local"})
public class LocalDynamoConfig {

  @Autowired AmazonDynamoDBAsync dynamoDB;

  @Autowired DynamoDBMapper dynamoDBMapper;

  @PostConstruct
  @Profile({"!test"})
  public void setDynamoDB() {
    CreateTableRequest createAuction =
        dynamoDBMapper
            .generateCreateTableRequest(Auction.class)
            .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

    CreateTableRequest createBidHistory =
        dynamoDBMapper
            .generateCreateTableRequest(BidHistory.class)
            .withProvisionedThroughput(new ProvisionedThroughput(100L, 100L));

    CreateTableRequest createAuctionHistory =
        dynamoDBMapper
            .generateCreateTableRequest(AuctionHistory.class)
            .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

    createBidHistory
        .getGlobalSecondaryIndexes()
        .forEach(
            idx ->
                idx.withProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L))
                    .withProjection(new Projection().withProjectionType("ALL")));
    createAuctionHistory
        .getGlobalSecondaryIndexes()
        .forEach(
            idx ->
                idx.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                    .withProjection(new Projection().withProjectionType("ALL")));
    TableUtils.createTableIfNotExists(dynamoDB, createAuction);
    TableUtils.createTableIfNotExists(dynamoDB, createBidHistory);
    TableUtils.createTableIfNotExists(dynamoDB, createAuctionHistory);
  }

  @PreDestroy
  public void deleteDynamoDB() {
    TableUtils.deleteTableIfExists(
        dynamoDB, dynamoDBMapper.generateDeleteTableRequest(AuctionHistory.class));
    TableUtils.deleteTableIfExists(
        dynamoDB, dynamoDBMapper.generateDeleteTableRequest(Auction.class));
    TableUtils.deleteTableIfExists(
        dynamoDB, dynamoDBMapper.generateDeleteTableRequest(BidHistory.class));
  }
}
