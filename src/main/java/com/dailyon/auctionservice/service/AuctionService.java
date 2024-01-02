package com.dailyon.auctionservice.service;

import com.dailyon.auctionservice.common.feign.response.CreateProductResponse;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
