package com.dailyon.auctionservice.service;

import com.dailyon.auctionservice.common.feign.response.CreateProductResponse;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse.ReadAuctionResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;

    @Transactional
    public Auction create(CreateAuctionRequest auctionRequest, CreateProductResponse response) {
        return auctionRepository.save(
                Auction.create(
                        response.getProductId(),
                        auctionRequest.getAuctionName(),
                        auctionRequest.getStartBidPrice(),
                        auctionRequest.getMaximumWinner(),
                        auctionRequest.getStartAt()
                )
        );
    }

    public Page<Auction> readAuctionsForAdmin(Pageable pageable) {
        int currentPage = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        int startIdx = currentPage * pageSize;
        int endIdx = startIdx + pageSize;

        List<Auction> auctions = auctionRepository.findAll();
        if(auctions.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int totalSize = auctions.size();

        List<Auction> sorted = auctions.stream()
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
        if(auctions.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int totalSize = auctions.size();

        List<Auction> sorted = auctions.stream()
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
        if(auctions.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int totalSize = auctions.size();

        List<Auction> sorted = auctions.stream()
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
        if(auctions.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int totalSize = auctions.size();

        List<Auction> sorted = auctions.stream()
                .sorted(Auction::compareTo)
                .collect(Collectors.toList())
                .subList(startIdx, Math.min(endIdx, totalSize));

        return new PageImpl<>(sorted, pageable, totalSize);
    }

    public ReadAuctionResponse readAuctionDetail(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 경매입니다"));

        return ReadAuctionResponse.of(auction);
    }
}
