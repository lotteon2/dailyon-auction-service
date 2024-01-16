package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.document.AuctionHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadAuctionHistoryPageResponse {
    private long totalElements;
    private int totalPages;
    private List<ReadAuctionHistoryResponse> responses;

    public static ReadAuctionHistoryPageResponse of(Page<AuctionHistory> histories) {
        return ReadAuctionHistoryPageResponse.builder()
                .totalPages(histories.getTotalPages())
                .totalPages(histories.getTotalPages())
                .responses(histories.stream()
                        .map(ReadAuctionHistoryResponse::of)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadAuctionHistoryResponse {
        private String id;
        private String auctionId;
        private Long auctionProductId;
        private String auctionProductImg;
        private String auctionProductName;
        private Long auctionProductSizeId;
        private String auctionProductSizeName;
        private boolean isWinner;
        private boolean isPaid;
        private Long amountToPay;
        private Long memberHighestBid;
        private Long auctionWinnerBid;

        public static ReadAuctionHistoryResponse of(AuctionHistory auctionHistory) {
            return ReadAuctionHistoryResponse.builder()
                    .id(auctionHistory.getId())
                    .auctionId(auctionHistory.getAuctionId())
                    .auctionProductId(auctionHistory.getAuctionProductId())
                    .auctionProductImg(auctionHistory.getAuctionProductImg())
                    .auctionProductName(auctionHistory.getAuctionProductName())
                    .auctionProductSizeId(auctionHistory.getAuctionProductSizeId())
                    .auctionProductSizeName(auctionHistory.getAuctionProductSizeName())
                    .isWinner(auctionHistory.isWinner())
                    .isPaid(auctionHistory.isPaid())
                    .amountToPay(auctionHistory.getAmountToPay())
                    .memberHighestBid(auctionHistory.getMemberHighestBid())
                    .auctionWinnerBid(auctionHistory.getAuctionWinnerBid())
                    .build();
        }
    }
}
