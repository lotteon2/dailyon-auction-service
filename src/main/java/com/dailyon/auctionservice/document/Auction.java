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
@DynamoDBTable(tableName = "auctions")
public class Auction implements Comparable<Auction> {
    @Id
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute(attributeName = "auction_product_id")
    private Long auctionProductId;

    @DynamoDBAttribute(attributeName = "auction_name")
    private String auctionName;

    @DynamoDBAttribute(attributeName = "start_bid_price")
    private Integer startBidPrice;

    @DynamoDBAttribute(attributeName = "maximum_winner")
    private Integer maximumWinner;

    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "start_at")
    private LocalDateTime startAt;

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    @DynamoDBAttribute(attributeName = "is_started")
    @Builder.Default
    private boolean isStarted = false;

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    @DynamoDBAttribute(attributeName = "is_ended")
    @Builder.Default
    private boolean isEnded = false;

    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Auction create(
            Long auctionProductId,
            String auctionName,
            Integer startBidPrice,
            Integer maximumWinner,
            LocalDateTime startAt
    ) {
        return Auction.builder()
                .auctionProductId(auctionProductId)
                .auctionName(auctionName)
                .startBidPrice(startBidPrice)
                .maximumWinner(maximumWinner)
                .startAt(startAt)
                .build();
    }

    @Override
    public int compareTo(Auction o) {
        return o.getCreatedAt().compareTo(this.getCreatedAt());
    }
}
