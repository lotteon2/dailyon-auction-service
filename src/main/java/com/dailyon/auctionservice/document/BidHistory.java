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

  @Id
  @DynamoDBHashKey(attributeName = "member_id")
  private String memberId;

  @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
  @DynamoDBRangeKey(attributeName = "created_at")
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  @DynamoDBIndexHashKey(attributeName = "auction_id")
  private String auctionId;

  @DynamoDBIndexHashKey(attributeName = "round")
  private Integer round;

  @DynamoDBAttribute(attributeName = "bid_amount")
  private Long bidAmount;
}
