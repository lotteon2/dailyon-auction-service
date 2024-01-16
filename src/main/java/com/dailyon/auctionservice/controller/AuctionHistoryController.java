package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.response.ReadAuctionHistoryPageResponse;
import com.dailyon.auctionservice.facade.AuctionHistoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions/history")
public class AuctionHistoryController {
    private final AuctionHistoryFacade auctionHistoryFacade;

    @GetMapping
    Mono<ReadAuctionHistoryPageResponse> readAuctionHistory(
            @RequestHeader(name = "memberId") String memberId,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size
    ) {
        return Mono.just(auctionHistoryFacade.readAuctionHistories(memberId, PageRequest.of(page, size)));
    }
}
