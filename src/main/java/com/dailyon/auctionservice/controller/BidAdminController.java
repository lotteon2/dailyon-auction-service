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

  @GetMapping("/bids/start")
  public Mono<Void> start(@RequestHeader(name = "role") String role) {
    bidFacade.start();
    return Mono.empty();
  }
}
