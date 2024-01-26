package com.dailyon.auctionservice.service;

import com.dailyon.auctionservice.common.webclient.response.CreateProductResponse;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse.ReadAuctionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {
  private final AuctionRepository auctionRepository;

  public Auction create(CreateAuctionRequest auctionRequest, CreateProductResponse response) {
    return auctionRepository.save(
        Auction.create(
            response.getProductId(),
            auctionRequest.getAuctionName(),
            auctionRequest.getStartBidPrice(),
            auctionRequest.getAskingPrice(),
            auctionRequest.getMaximumWinner(),
            auctionRequest.getStartAt()));
  }

  public Page<Auction> readAuctionsForAdmin(Pageable pageable) {
    int currentPage = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    int startIdx = currentPage * pageSize;
    int endIdx = startIdx + pageSize;

    List<Auction> auctions = auctionRepository.findAll();
    if (auctions.isEmpty()) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    int totalSize = auctions.size();

    List<Auction> sorted =
        auctions.stream()
            .sorted(Auction::compareTo)
            .collect(Collectors.toList())
            .subList(startIdx, Math.min(endIdx, totalSize));

    return new PageImpl<>(sorted, pageable, totalSize);
  }

  public Page<Auction> readPastAuctions(Pageable pageable) {
    int currentPage = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    int startIdx = currentPage * pageSize;
    int endIdx = startIdx + pageSize;

    List<Auction> auctions = auctionRepository.findAuctionsByStartedAndEnded(true, true);
    if (auctions.isEmpty()) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    int totalSize = auctions.size();

    List<Auction> sorted =
        auctions.stream()
            .sorted(Auction::compareTo)
            .collect(Collectors.toList())
            .subList(startIdx, Math.min(endIdx, totalSize));

    return new PageImpl<>(sorted, pageable, totalSize);
  }

  public Page<Auction> readFutureAuctions(Pageable pageable) {
    int currentPage = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    int startIdx = currentPage * pageSize;
    int endIdx = startIdx + pageSize;

    List<Auction> auctions = auctionRepository.findAuctionsByStartedAndEnded(false, false);
    if (auctions.isEmpty()) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    int totalSize = auctions.size();

    List<Auction> sorted =
        auctions.stream()
            .sorted(Auction::compareTo)
            .collect(Collectors.toList())
            .subList(startIdx, Math.min(endIdx, totalSize));

    return new PageImpl<>(sorted, pageable, totalSize);
  }

  public Page<Auction> readCurrentAuctions(Pageable pageable) {
    int currentPage = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    int startIdx = currentPage * pageSize;
    int endIdx = startIdx + pageSize;

    List<Auction> auctions = auctionRepository.findAuctionsByStartedAndEnded(true, false);
    if (auctions.isEmpty()) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    int totalSize = auctions.size();

    List<Auction> sorted =
        auctions.stream()
            .sorted(Auction::compareTo)
            .collect(Collectors.toList())
            .subList(startIdx, Math.min(endIdx, totalSize));

    return new PageImpl<>(sorted, pageable, totalSize);
  }

  public ReadAuctionResponse readAuctionDetail(String auctionId) {
    Auction auction =
        auctionRepository
            .findById(auctionId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 경매입니다"));

    return ReadAuctionResponse.of(auction);
  }

  public Auction readAuction(String auctionId) {
    return auctionRepository
        .findById(auctionId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 경매입니다"));
  }

  public Mono<Auction> startAuction(String auctionId) {
    return Mono.justOrEmpty(auctionRepository.findById(auctionId))
        .switchIfEmpty(Mono.error(new RuntimeException("존재하지 않는 경매입니다")))
        .flatMap(
            auction -> {
              log.info("localDateTime now : {}", LocalDateTime.now());
              log.info("auction get startAt : {}",auction.getStartAt());
              // 현재 시각이 경매 시작 시간과 같거나 이후이고, 아직 시작되지 않고, 끝나지 않은 경매라면 시작 가능
              if ((LocalDateTime.now().isEqual(auction.getStartAt())
                      || LocalDateTime.now().isAfter(auction.getStartAt()))
                  && (!auction.isStarted() && !auction.isEnded())) {
                auction.setStarted(true);
                return Mono.justOrEmpty(auctionRepository.save(auction));

              } else {
                return Mono.error(new RuntimeException("시작 가능한 상태가 아닙니다"));
              }
            });
  }

  public Mono<Auction> endAuction(String auctionId) {
    log.info("auctionId {}", auctionId);
    return Mono.justOrEmpty(auctionRepository.findById(auctionId))
        .switchIfEmpty(Mono.error(new RuntimeException("존재하지 않는 경매입니다")))
        .flatMap(
            auction -> {
              // 아직 시작 가능 시간 전이라면 시작 불가
              if (!auction.isStarted() || auction.isEnded()) {
                return Mono.error(new RuntimeException("종료 가능한 상태가 아닙니다"));
              }
              auction.setEnded(true);
              return Mono.fromCallable(() -> auctionRepository.save(auction)).thenReturn(auction);
            });
  }

  public void deleteAuction(String auctionId) {
    auctionRepository.deleteById(auctionId);
  }

  public void delete(Auction auction) {
    auctionRepository.delete(auction);
  }
}
