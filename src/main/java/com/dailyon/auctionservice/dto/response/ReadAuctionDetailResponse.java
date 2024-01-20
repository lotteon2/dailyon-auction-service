package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.document.Auction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadAuctionDetailResponse {
    private ReadAuctionResponse auctionResponse;
    private ReadProductDetailResponse productDetailResponse;

    public static ReadAuctionDetailResponse of(
            ReadAuctionResponse auctionResponse,
            ReadProductDetailResponse productDetailResponse
    ) {
        return ReadAuctionDetailResponse.builder()
                .auctionResponse(auctionResponse)
                .productDetailResponse(productDetailResponse)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadAuctionResponse {
        private String id;
        private Long auctionProductId;
        private String auctionName;
        private Integer startBidPrice;
        private Integer askingPrice;
        private Integer maximumWinner;
        private LocalDateTime startAt;
        private boolean isStarted;
        private boolean isEnded;

        public static ReadAuctionResponse of(Auction auction) {
            return ReadAuctionResponse.builder()
                    .id(auction.getId())
                    .auctionProductId(auction.getAuctionProductId())
                    .auctionName(auction.getAuctionName())
                    .startBidPrice(auction.getStartBidPrice())
                    .askingPrice(auction.getAskingPrice())
                    .maximumWinner(auction.getMaximumWinner())
                    .startAt(auction.getStartAt())
                    .isStarted(auction.isStarted())
                    .isEnded(auction.isEnded())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadProductDetailResponse {
        private Long categoryId;
        private Long brandId;
        private String brandName;
        private String name;
        private String imgUrl;
        private Integer price;
        private String gender;

        private Double avgRating;
        private Long reviewCount;

        private List<ReadProductStockResponse> productStocks;
        private List<String> describeImgUrls;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ReadProductStockResponse {
            private Long productSizeId;
            private String productSizeName;
            private Long quantity;
        }
    }
}
