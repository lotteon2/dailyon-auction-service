package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.response.EnterResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse;
import com.dailyon.auctionservice.facade.AuctionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin("*")
@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {
  private final AuctionFacade auctionFacade;

  @GetMapping("/future")
  public Mono<ReadAuctionPageResponse> readFutureAuctions(
      @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
    return Mono.just(auctionFacade.readFutureAuctions(PageRequest.of(page, size)));
  }

  @GetMapping("/current")
  public Mono<ReadAuctionPageResponse> readCurrentAuctions(
      @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
    return Mono.just(auctionFacade.readCurrentAuctions(PageRequest.of(page, size)));
  }

  @GetMapping("/past")
  public Mono<ReadAuctionPageResponse> readPastAuctions(
      @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
    return Mono.just(auctionFacade.readPastAuctions(PageRequest.of(page, size)));
  }

  @GetMapping("/detail/{auctionId}")
  public Mono<ReadAuctionDetailResponse> readAuctionDetail(@PathVariable String auctionId) {
    return auctionFacade.readAuctionDetail(auctionId);
  }

  @GetMapping("/enter/{auctionId}")
  public Mono<EnterResponse> enter(
      @PathVariable String auctionId, @RequestHeader("memberId") Long memberId) {
    Mono<String> token = Mono.just(auctionFacade.createToken(memberId));
    Mono<ReadAuctionDetailResponse> readAuctionDetailResponseMono =
        auctionFacade.readAuctionDetail(auctionId);

    return Mono.zip(token, readAuctionDetailResponseMono)
        .map(tuple -> EnterResponse.of(tuple.getT1(), tuple.getT2()));
  }
}
