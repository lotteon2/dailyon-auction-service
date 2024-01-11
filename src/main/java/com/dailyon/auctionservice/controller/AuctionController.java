package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse;
import com.dailyon.auctionservice.facade.AuctionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@CrossOrigin("*")
@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionFacade auctionFacade;

    @GetMapping("/future")
    public Mono<ReadAuctionPageResponse> readFutureAuctions(@RequestParam(name = "page") int page,
                                                            @RequestParam(name = "size") int size) {
        return Mono.just(auctionFacade.readFutureAuctions(PageRequest.of(page, size)));
    }

    @GetMapping("/current")
    public Mono<ReadAuctionPageResponse> readCurrentAuctions(@RequestParam(name = "page") int page,
                                                             @RequestParam(name = "size") int size) {
        return Mono.just(auctionFacade.readCurrentAuctions(PageRequest.of(page, size)));
    }

    @GetMapping("/past")
    public Mono<ReadAuctionPageResponse> readPastAuctions(@RequestParam(name = "page") int page,
                                                          @RequestParam(name = "size") int size) {
        return Mono.just(auctionFacade.readPastAuctions(PageRequest.of(page, size)));
    }

    @GetMapping("/detail/{auctionId}")
    public Mono<ReadAuctionDetailResponse> readAuctionDetail(@PathVariable String auctionId) {
        return auctionFacade.readAuctionDetail(auctionId);
    }
}
