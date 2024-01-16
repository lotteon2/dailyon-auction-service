package com.dailyon.auctionservice.repository;

import com.dailyon.auctionservice.document.BidHistory;
import com.dailyon.auctionservice.dto.request.CreateBidRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Profile("!test")
@Repository
public class ReactiveRedisRepository {
  private static final String AUCTION_KEY = "auction_id:";
  private static final String ROUND_KEY = ":round:";
  private final ReactiveZSetOperations reactiveRedisZSet;
  private final ReactiveRedisTemplate<String, BidHistory> reactiveRedisTemplate;

  public ReactiveRedisRepository(
      @Qualifier("reactiveRedisTemplateForBid")
          ReactiveRedisTemplate<String, BidHistory> reactiveRedisTemplate) {
    this.reactiveRedisZSet = reactiveRedisTemplate.opsForZSet();
    this.reactiveRedisTemplate = reactiveRedisTemplate;
  }

  public Mono<Void> save(BidHistory history) {
    String key = generateKey(history.getAuctionId(), history.getRound());
    return reactiveRedisZSet
        .add(key, history, history.getBidAmount())
        .flatMap(success -> reactiveRedisTemplate.expire(key, Duration.ofHours(1L)))
        .then();
  }

  public Flux<BidHistory> getTopBidder(CreateBidRequest request, int maximum) {
    String key = generateKey(request.getAuctionId(), request.getRound());
    return reactiveRedisZSet.reverseRange(
        key, Range.from(Range.Bound.inclusive(0L)).to(Range.Bound.inclusive((long) maximum-1)));
  }

  private String generateKey(String auctionId, String round) {
    return AUCTION_KEY + auctionId + ROUND_KEY + round;
  }
}
