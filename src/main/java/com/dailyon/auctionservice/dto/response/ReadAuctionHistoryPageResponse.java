package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.document.AuctionHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadAuctionHistoryPageResponse {
    private long totalElements;
    private int totalPages;
    private List<AuctionHistory> responses;

    public static ReadAuctionHistoryPageResponse of(Page<AuctionHistory> histories) {
        return ReadAuctionHistoryPageResponse.builder()
                .totalPages(histories.getTotalPages())
                .totalPages(histories.getTotalPages())
                .responses(histories.toList())
                .build();
    }
}
