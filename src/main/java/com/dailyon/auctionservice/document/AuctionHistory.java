package com.dailyon.auctionservice.document;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.dailyon.auctionservice.config.DynamoDbConfig;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "auction_history")
public class AuctionHistory implements Comparable<AuctionHistory> {
    @Id @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBIndexHashKey(
            attributeName = "member_id",
            globalSecondaryIndexName = "memberAuctionHistoryIdx"
    )
    private String memberId;

    @DynamoDBIndexRangeKey(
            attributeName = "auction_id",
            globalSecondaryIndexName = "memberAuctionHistoryIdx"
    )
    private String auctionId;

    @DynamoDBAttribute(attributeName = "auction_name")
    private String auctionName;

    @DynamoDBAttribute(attributeName = "auction_product_id")
    private Long auctionProductId;

    @DynamoDBAttribute(attributeName = "auction_product_img")
    private String auctionProductImg;

    @DynamoDBAttribute(attributeName = "auction_product_name")
    private String auctionProductName;

    @DynamoDBAttribute(attributeName = "auction_product_size_id")
    private Long auctionProductSizeId;

    @DynamoDBAttribute(attributeName = "auction_product_size_name")
    private String auctionProductSizeName;

    // 유저의 낙찰 여부
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    @DynamoDBAttribute(attributeName = "is_winner")
    private boolean isWinner;

    // 낙찰된 유저가 나머지 95% 결제했는지
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    @DynamoDBAttribute(attributeName = "is_paid")
    @Builder.Default
    private boolean isPaid = false;

    // 결제해야 할 95%에 해당하는 금액
    @DynamoDBAttribute(attributeName = "amount_to_pay")
    private Long amountToPay;

    // 유저의 최대 bid 금액
    @DynamoDBAttribute(attributeName = "member_highest_bid")
    private Long memberHighestBid;

    // 낙찰 bid 금액
    @DynamoDBAttribute(attributeName = "auction_winner_bid")
    private Long auctionWinnerBid;

    @DynamoDBAttribute(attributeName = "created_at")
    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static AuctionHistory create(
            String memberId,
            String auctionId,
            String auctionName,
            Long auctionProductId,
            String auctionProductImg,
            String auctionProductName,
            Long auctionProductSizeId,
            String auctionProductSizeName,
            boolean isWinner,
            Long amountToPay,
            Long memberHighestBid,
            Long auctionWinnerBid
    ) {
        return AuctionHistory.builder()
                .memberId(memberId)
                .auctionId(auctionId)
                .auctionName(auctionName)
                .auctionProductId(auctionProductId)
                .auctionProductImg(auctionProductImg)
                .auctionProductName(auctionProductName)
                .auctionProductSizeId(auctionProductSizeId)
                .auctionProductSizeName(auctionProductSizeName)
                .isWinner(isWinner)
                .amountToPay(amountToPay)
                .memberHighestBid(memberHighestBid)
                .auctionWinnerBid(auctionWinnerBid)
                .build();
    }

    @Override
    public int compareTo(AuctionHistory o) {
        return o.createdAt.compareTo(this.createdAt);
    }
}
