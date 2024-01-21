package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.response.AuctionProductInfo;
import com.dailyon.auctionservice.facade.AuctionHistoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/clients/auction-histories")
public class AuctionHistoryFeignApiController {
  private final AuctionHistoryFacade auctionHistoryFacade;

  @GetMapping("/{auctionId}")
  public Mono<AuctionProductInfo> getAuctionHistory(
      @RequestHeader("memberId") Long memberId, @PathVariable("auctionId") String auctionId) {
    return auctionHistoryFacade.readAuctionHistoryAndProductDetail(memberId, auctionId);
  }
}
