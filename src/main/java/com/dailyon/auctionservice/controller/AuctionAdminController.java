package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.dto.response.CreateAuctionResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse;
import com.dailyon.auctionservice.facade.AuctionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AuctionAdminController {
    private final AuctionFacade auctionFacade;

    // blocking I/O
    @PostMapping("/auction")
    public ResponseEntity<CreateAuctionResponse> createAuction(@Valid @RequestBody CreateAuctionRequest createAuctionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionFacade.createAuction(createAuctionRequest));
    }

    @GetMapping("/auction")
    public Mono<ReadAuctionPageResponse> readAuctionsForAdmin(Pageable pageable) {
        return Mono.just(auctionFacade.readAuctionsForAdmin(pageable));
    }
}
