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
@DynamoDBTable(tableName = "auction_chat")
public class AuctionChat {
  @Id
  @DynamoDBHashKey(attributeName = "auction_id")
  private String auctionId;

  @DynamoDBRangeKey(attributeName = "message_id")
  @DynamoDBAutoGeneratedKey
  private String messageId;

  @DynamoDBAttribute(attributeName = "member_id")
  @DynamoDBIndexHashKey(globalSecondaryIndexName = "memberIndex")
  private Long memberId;

  @DynamoDBAttribute(attributeName = "message")
  private String message;

  @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
  @DynamoDBAttribute(attributeName = "created_at")
  @DynamoDBIndexRangeKey(globalSecondaryIndexName = "memberIndex")
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}
