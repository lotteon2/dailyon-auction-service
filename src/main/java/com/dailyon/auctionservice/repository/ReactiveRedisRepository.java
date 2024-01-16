package com.dailyon.auctionservice.repository;

import com.dailyon.auctionservice.document.BidHistory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

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
    String key = generateKey(history);
    return reactiveRedisZSet
        .add(key, history, history.getBidAmount())
        .flatMap(success -> reactiveRedisTemplate.expire(key, Duration.ofHours(1L)))
        .then();
  }

  public Mono<List<BidHistory>> getTopBidder(BidHistory history, int range) {
    String key = generateKey(history);
    return null;
  }

  private String generateKey(BidHistory history) {
    return AUCTION_KEY + history.getAuctionId() + ROUND_KEY + history.getRound();
  }
}
