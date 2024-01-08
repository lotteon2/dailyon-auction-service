package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse;
import com.dailyon.auctionservice.facade.AuctionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import static com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse.ReadAuctionResponse;


@CrossOrigin("*")
@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionFacade auctionFacade;

    @GetMapping("/future")
    public Mono<ReadAuctionPageResponse> readFutureAuctions(Pageable pageable) {
        return Mono.just(auctionFacade.readFutureAuctions(pageable));
    }

    @GetMapping("/current")
    public Mono<ReadAuctionPageResponse> readCurrentAuctions(Pageable pageable) {
        return Mono.just(auctionFacade.readCurrentAuctions(pageable));
    }

    @GetMapping("/past")
    public Mono<ReadAuctionPageResponse> readPastAuctions(Pageable pageable) {
        return Mono.just(auctionFacade.readPastAuctions(pageable));
    }

    @GetMapping("/detail/{auctionId}")
    public Mono<ReadAuctionResponse> readAuctionDetail(@PathVariable String auctionId) {
        return Mono.just(auctionFacade.readAuctionDetail(auctionId));
    }
}
