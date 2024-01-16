package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.dto.response.CreateAuctionResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse;
import com.dailyon.auctionservice.facade.AuctionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AuctionAdminController {
    private final AuctionFacade auctionFacade;

    @PostMapping("/auctions")
    public Mono<CreateAuctionResponse> createAuction(
            @RequestHeader(name = "memberId") String memberId,
            @RequestHeader(name = "role") String role,
            @Valid @RequestBody CreateAuctionRequest createAuctionRequest) {
        return auctionFacade.createAuction(memberId, role, createAuctionRequest);
    }

    @GetMapping("/auctions")
    public Mono<ReadAuctionPageResponse> readAuctionsForAdmin(@RequestParam(name = "page") int page,
                                                              @RequestParam(name = "size") int size) {
        return Mono.just(auctionFacade.readAuctionsForAdmin(PageRequest.of(page, size)));
    }
}
