package com.dailyon.auctionservice.document;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dailyon.auctionservice.ContainerBaseTestSupport;
import com.dailyon.auctionservice.repository.BidHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.LocalDateTime;
import java.util.List;

class BidHistoryTest extends ContainerBaseTestSupport {

  @Autowired AmazonDynamoDBAsync dynamoDB;
  @Autowired DynamoDBMapper dynamoDBMapper;
  @Autowired BidHistoryRepository bidHistoryRepository;

  @BeforeEach
  void beforeEach() {
    CreateTableRequest createTableRequest =
        dynamoDBMapper
            .generateCreateTableRequest(BidHistory.class)
            .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

    createTableRequest
        .getGlobalSecondaryIndexes()
        .forEach(
            idx ->
                idx.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                    .withProjection(new Projection().withProjectionType("ALL")));
    TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
  }

  @AfterEach
  void afterEach() {
    TableUtils.deleteTableIfExists(
        dynamoDB, dynamoDBMapper.generateDeleteTableRequest(BidHistory.class));
  }

  @DisplayName("absdasdsadad")
  @Test
  void test() {
    // given

    LocalDateTime now = LocalDateTime.now();
    final BidHistory.PrimaryKey myEntityPK = new BidHistory.PrimaryKey("partitionKey", now);
    BidHistory bid =
        BidHistory.builder()
            .round("1")
            .auctionId("bid")
            .bidAmount(10000L)
            .primaryKey(myEntityPK)
            .build();
    bidHistoryRepository.save(bid);

    List<BidHistory> bid1 = bidHistoryRepository.findAllByAuctionIdAndRound("bid", "1");
    System.out.println(bid1.get(0).getCreatedAt());
//    DynamoDBQueryExpression<BidHistory> queryExpression =
//        new DynamoDBQueryExpression<BidHistory>()
//            .withIndexName("auctionRoundIndex")
//            .withConsistentRead(false)
//            .withKeyConditionExpression("auction_id = :v_bid and round = :v_1")
//            .withExpressionAttributeValues(
//                ImmutableMap.of(
//                    ":v_bid", new AttributeValue().withS("bid"),
//                    ":v_1", new AttributeValue().withS("1")));
//
//    List<BidHistory> bidHistoryList = dynamoDBMapper.query(BidHistory.class, queryExpression);
    // when

    // then
  }
}
