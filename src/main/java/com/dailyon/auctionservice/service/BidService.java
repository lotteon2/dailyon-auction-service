package com.dailyon.auctionservice.service;

import com.dailyon.auctionservice.common.webclient.client.ProductClient;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.document.AuctionHistory;
import com.dailyon.auctionservice.document.BidHistory;
import com.dailyon.auctionservice.dto.request.CreateBidRequest;
import com.dailyon.auctionservice.dto.response.BidInfo;
import com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse;
import com.dailyon.auctionservice.dto.response.TopBidderResponse;
import com.dailyon.auctionservice.repository.AuctionHistoryRepository;
import com.dailyon.auctionservice.repository.AuctionRepository;
import com.dailyon.auctionservice.repository.BidHistoryRepository;
import com.dailyon.auctionservice.repository.ReactiveRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {
  private final BidHistoryRepository bidHistoryRepository;
  private final ReactiveRedisRepository reactiveRedisRepository;
  private final AuctionRepository auctionRepository;
  private final ProductClient productClient;
  private final AuctionHistoryRepository auctionHistoryRepository;

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
                          return reactiveRedisRepository
                              .saveMemberId(auction.getId(), memberId)
                              .thenReturn(bidAmount.longValue());
                        }));
  }

  public Mono<List<TopBidderResponse>> getTopBidder(CreateBidRequest request, int maximumWinner) {
    return reactiveRedisRepository.getTopBidder(request, maximumWinner).collectList();
  }

  public Mono<Void> createAuctionHistories(Auction auction) {
    Mono<Long> auctionWinnerBid =
        reactiveRedisRepository
            .getTopScore(auction.getId(), auction.getMaximumWinner())
            .map(Double::longValue);

    Mono<ReadAuctionDetailResponse.ReadProductDetailResponse> productInfo =
        productClient.readProductDetail(auction.getAuctionProductId());

    Mono<Void> saveSuccessfulBiddersHistory =
        productInfo
            .zipWith(auctionWinnerBid)
            .flatMapMany(
                tuple -> createAndSaveAuctionHistories(auction, tuple.getT1(), tuple.getT2(), true))
            .then();

    Mono<Void> saveRemainBiddersHistory =
        productInfo
            .zipWith(auctionWinnerBid)
            .flatMapMany(
                tuple ->
                    createAndSaveAuctionHistories(auction, tuple.getT1(), tuple.getT2(), false))
            .then();
    
    // sqs 들어갈자리
    
    return Mono.when(saveSuccessfulBiddersHistory, saveRemainBiddersHistory);
  }

  private Flux<Void> createAndSaveAuctionHistories(
      Auction auction,
      ReadAuctionDetailResponse.ReadProductDetailResponse product,
      long auctionWinnerBid,
      boolean isSuccessful) {
    Flux<ZSetOperations.TypedTuple<BidInfo>> bidders =
        isSuccessful
            ? reactiveRedisRepository.getSuccessfulBidInfos(auction)
            : reactiveRedisRepository.getRemainBidInfos(auction);

    return bidders.flatMap(
        tuple -> {
          BidInfo value = tuple.getValue();
          AuctionHistory auctionHistory =
              value.createAuctionHistory(
                  auction, product, tuple.getScore().longValue(), auctionWinnerBid, isSuccessful);
          return Mono.fromRunnable(() -> auctionHistoryRepository.save(auctionHistory));
        });
  }
}
