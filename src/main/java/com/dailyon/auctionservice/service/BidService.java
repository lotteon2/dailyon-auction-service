package com.dailyon.auctionservice.service;

import com.dailyon.auctionservice.document.BidHistory;
import com.dailyon.auctionservice.dto.request.CreateBidRequest;
import com.dailyon.auctionservice.dto.response.TopBidderResponse;
import com.dailyon.auctionservice.repository.AuctionRepository;
import com.dailyon.auctionservice.repository.BidHistoryRepository;
import com.dailyon.auctionservice.repository.ReactiveRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {
  private final BidHistoryRepository bidHistoryRepository;
  private final ReactiveRedisRepository reactiveRedisRepository;
  private final AuctionRepository auctionRepository;

  public Mono<Long> create(CreateBidRequest request, String memberId) {
    BidHistory bidHistory = request.toEntity(memberId);
    return reactiveRedisRepository
        .save(bidHistory)
        .then(Mono.just(bidHistoryRepository.save(bidHistory)))
        .map(BidHistory::getBidAmount);
  }
}
