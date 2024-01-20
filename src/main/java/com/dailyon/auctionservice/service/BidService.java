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

    return Mono.justOrEmpty(
            auctionRepository
                .findById(request.getAuctionId())
                .orElseThrow(() -> new RuntimeException("해당 경매정보가 존재하지 않습니다.")))
        .flatMap(
            auction ->
                reactiveRedisRepository
                    .save(bidHistory, auction, request.isInputCheck())
                    .flatMap(
                        bidAmount -> {
                          bidHistory.setBidAmount(bidAmount.longValue());
                          bidHistoryRepository.save(bidHistory);
                          return Mono.just(bidAmount.longValue());
                        }));
  }

  public Mono<List<TopBidderResponse>> getTopBidder(CreateBidRequest request, int maximumWinner) {
    return reactiveRedisRepository.getTopBidder(request, maximumWinner).collectList();
  }
}
