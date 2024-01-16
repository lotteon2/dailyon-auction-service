package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.dto.request.CreateBidRequest;
import com.dailyon.auctionservice.facade.BidFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin("*")
@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidApiController {

  private final BidFacade bidFacade;

  @PostMapping("")
  public Mono<Long> bidding(
      @RequestHeader("memberId") String memberId, @RequestBody CreateBidRequest request) {
    return bidFacade.createBid(request, memberId);
  }
}
