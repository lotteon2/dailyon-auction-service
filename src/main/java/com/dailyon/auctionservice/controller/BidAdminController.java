package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.facade.BidFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BidAdminController {

  private final BidFacade bidFacade;

  // 경매 시작
  @PatchMapping("/bids/start/{auctionId}")
  public Mono<Void> start(
          @RequestHeader(name = "role") String role,
          @PathVariable String auctionId
  ) {
    return bidFacade.start(auctionId);
  }

  // 경매 종료
//  @PatchMapping("/bids/end/{auctionId}")
//  public Mono<Void> end(
//          @RequestHeader(name = "role") String role,
//          @PathVariable String auctionId
//  ) {
//    return bidFacade.end(auctionId);
//  }
}
