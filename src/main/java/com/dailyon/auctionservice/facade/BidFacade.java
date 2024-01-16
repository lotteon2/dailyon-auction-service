package com.dailyon.auctionservice.facade;

import com.dailyon.auctionservice.chat.response.ChatCommand;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.controller.ChatHandler;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateBidRequest;
import com.dailyon.auctionservice.service.AuctionService;
import com.dailyon.auctionservice.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BidFacade {
  private final BidService bidService;
  private final AuctionService auctionService;
  private final ChatHandler chatHandler;

  public Mono<Long> createBid(CreateBidRequest request, String memberId) {
    Auction auction = auctionService.readAuction(request.getAuctionId());
    Mono<Long> bidAmount = bidService.create(request, memberId);

    Integer maximumWinner = auction.getMaximumWinner();
    return bidService.create(request, memberId);
  }

  public void start() {
    ChatPayload<Object> payload = ChatPayload.of(ChatCommand.START, null);
    chatHandler.sendStart(payload);
  }
}
