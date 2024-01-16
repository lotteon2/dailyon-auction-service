package com.dailyon.auctionservice.facade;

import com.dailyon.auctionservice.chat.response.ChatCommand;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.controller.ChatHandler;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateBidRequest;
import com.dailyon.auctionservice.dto.response.TopBidderResponse;
import com.dailyon.auctionservice.service.AuctionService;
import com.dailyon.auctionservice.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidFacade {
  private final BidService bidService;
  private final AuctionService auctionService;
  private final ChatHandler chatHandler;

  public Mono<Long> createBid(CreateBidRequest request, String memberId) {
    Auction auction = auctionService.readAuction(request.getAuctionId());
    Integer maximumWinner = auction.getMaximumWinner();

    Mono<Long> bidAmountMono = bidService.create(request, memberId);
    Mono<List<TopBidderResponse>> topBidderMono = bidService.getTopBidder(request, maximumWinner);

    return bidAmountMono.flatMap(
        bidAmount ->
            topBidderMono.flatMap(
                topBidders -> {
                  ChatPayload<List<TopBidderResponse>> payload =
                      ChatPayload.of(ChatCommand.BIDDING, topBidders);
                  return chatHandler.biddingBroadCast(payload).thenReturn(bidAmount);
                }));
  }

  public void start() {
    ChatPayload<Object> payload = ChatPayload.of(ChatCommand.START, null);
    chatHandler.sendStart(payload);
  }
}