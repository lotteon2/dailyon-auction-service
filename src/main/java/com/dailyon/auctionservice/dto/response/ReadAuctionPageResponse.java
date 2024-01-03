package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.document.Auction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadAuctionPageResponse {
    private long totalElements;
    private int totalPages;
    private List<ReadAuctionResponse> responses;

    public static ReadAuctionPageResponse of(Page<Auction> auctions) {
        return ReadAuctionPageResponse.builder()
                .totalPages(auctions.getTotalPages())
                .totalElements(auctions.getTotalElements())
                .responses(auctions.stream()
                        .map(ReadAuctionResponse::of)
                        .collect(Collectors.toList()))
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
                    .maximumWinner(auction.getMaximumWinner())
                    .startAt(auction.getStartAt())
                    .isStarted(auction.isStarted())
                    .isEnded(auction.isEnded())
                    .build();
        }
    }
}
