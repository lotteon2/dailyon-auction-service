package com.dailyon.auctionservice.document;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.dailyon.auctionservice.config.DynamoDbConfig;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "bid_history")
public class BidHistory {

  @Id @DynamoDBIgnore private PrimaryKey primaryKey;

  @DynamoDBIndexHashKey(
      attributeName = "auction_id",
      globalSecondaryIndexName = "auctionRoundIndex")
  private String auctionId;

  @DynamoDBIndexRangeKey(attributeName = "round", globalSecondaryIndexName = "auctionRoundIndex")
  private String round;

  @DynamoDBAttribute(attributeName = "bid_amount")
  private Long bidAmount;

  @DynamoDBHashKey
  @DynamoDBAttribute(attributeName = "member_id")
  public String getMemberId() {
    return primaryKey != null ? primaryKey.getMemberId() : null;
  }

  public void setMemberId(final String partitionKey) {
    if (primaryKey == null) {
      primaryKey = new PrimaryKey();
    }
    primaryKey.setMemberId(partitionKey);
  }

  @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
  @DynamoDBRangeKey(attributeName = "created_at")
  public LocalDateTime getCreatedAt() {
    return primaryKey != null ? primaryKey.getCreatedAt() : null;
  }

  public void setCreatedAt(final LocalDateTime sortKey) {
    if (primaryKey == null) {
      primaryKey = new PrimaryKey();
    }
    primaryKey.setCreatedAt(sortKey);
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @DynamoDBDocument
  public static class PrimaryKey {
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "member_id")
    private String memberId;

    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
    @DynamoDBRangeKey(attributeName = "created_at")
    private LocalDateTime createdAt;
  }
}
