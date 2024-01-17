package com.dailyon.auctionservice.repository;

import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.document.BidHistory;
import com.dailyon.auctionservice.dto.request.CreateBidRequest;
import com.dailyon.auctionservice.dto.response.BidInfo;
import com.dailyon.auctionservice.dto.response.TopBidderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Repository
public class ReactiveRedisRepository {
  private static final String AUCTION_KEY = "auction_id:";
  private static final String MEMBER_ID = ":member_id:";
  private final ReactiveZSetOperations<String, BidInfo> reactiveRedisZSet;
  private final ReactiveRedisTemplate<String, BidInfo> reactiveRedisTemplate;

  public ReactiveRedisRepository(
      @Qualifier("reactiveRedisTemplateForBid")
          ReactiveRedisTemplate<String, BidInfo> reactiveRedisTemplate) {
    this.reactiveRedisZSet = reactiveRedisTemplate.opsForZSet();
    this.reactiveRedisTemplate = reactiveRedisTemplate;
  }

  public Mono<Double> save(BidHistory history, Auction auction) {
    String key = generateKey(history.getAuctionId());
    BidInfo bidInfo = BidInfo.from(history);
    long lowerBound = 0L;
    long upperBound = auction.getMaximumWinner() - 1;
    return reactiveRedisZSet
        .rank(key, bidInfo)
        .flatMap(
            rank -> {
              if (rank >= lowerBound && rank <= upperBound) {
                // value가 원하는 범위 내에 있으므로, score(bidAmount)에 auction.getAskingPrice() 값을 더한다.
                return reactiveRedisZSet.incrementScore(key, bidInfo, auction.getAskingPrice());
              } else {
                return Mono.empty();
              }
            })
        .switchIfEmpty(
            reactiveRedisZSet
                .add(key, bidInfo, history.getBidAmount())
                .thenReturn(history.getBidAmount().doubleValue()))
        .flatMap(
            bidAmount -> {
              reactiveRedisTemplate.expire(key, Duration.ofHours(1L));
              return Mono.just(bidAmount);
            });
  }

  public Flux<TopBidderResponse> getTopBidder(CreateBidRequest request, int maximum) {
    String key = generateKey(request.getAuctionId());
    return reactiveRedisZSet
        .reverseRange(
            key,
            Range.from(Range.Bound.inclusive(0L)).to(Range.Bound.inclusive((long) maximum - 1)))
        .flatMap(
            bidInfo ->
                reactiveRedisZSet
                    .score(key, bidInfo)
                    .map(score -> TopBidderResponse.from(bidInfo, Math.round(score))));
  }

  private String generateKey(String auctionId) {
    return AUCTION_KEY + auctionId;
  }
}
