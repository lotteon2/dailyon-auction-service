package com.dailyon.auctionservice.facade;

import com.dailyon.auctionservice.document.AuctionHistory;
import com.dailyon.auctionservice.service.AuctionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionHistoryFacade {
    private final AuctionHistoryService auctionHistoryService;

    public Page<AuctionHistory> readAuctionHistories(String memberId, Pageable pageable) {
        return auctionHistoryService.readAuctionHistories(memberId, pageable);
    }
}
