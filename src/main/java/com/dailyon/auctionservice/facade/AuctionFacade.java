package com.dailyon.auctionservice.facade;

import com.dailyon.auctionservice.common.webclient.client.ProductClient;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.dto.response.CreateAuctionResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse;
import com.dailyon.auctionservice.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionFacade {
    private final AuctionService auctionService;
    private final ProductClient productClient;

    public Mono<CreateAuctionResponse> createAuction(String memberId, String role, CreateAuctionRequest createAuctionRequest) {
        return productClient.createProduct(memberId, role, createAuctionRequest.getProductRequest()).flatMap(response -> {
            Auction auction = null;
            try {
                auction = auctionService.create(createAuctionRequest, response);
            } catch (Exception e) {
                productClient.deleteProducts(memberId, role, List.of(response.getProductId()));
            }
            return Mono.just(CreateAuctionResponse.create(auction, response));
        });
    }

    public ReadAuctionPageResponse readAuctionsForAdmin(Pageable pageable) {
        return ReadAuctionPageResponse.of(auctionService.readAuctionsForAdmin(pageable));
    }

    public ReadAuctionPageResponse readFutureAuctions(Pageable pageable) {
        return ReadAuctionPageResponse.of(auctionService.readFutureAuctions(pageable));
    }

    public ReadAuctionPageResponse readCurrentAuctions(Pageable pageable) {
        return ReadAuctionPageResponse.of(auctionService.readCurrentAuctions(pageable));
    }

    public ReadAuctionPageResponse readPastAuctions(Pageable pageable) {
        return ReadAuctionPageResponse.of(auctionService.readPastAuctions(pageable));
    }

    public Mono<ReadAuctionDetailResponse> readAuctionDetail(String auctionId) {
        Mono<ReadAuctionDetailResponse.ReadAuctionResponse> auctionDetail =
                Mono.just(auctionService.readAuctionDetail(auctionId));

        return auctionDetail.flatMap(auction -> {
            Mono<ReadAuctionDetailResponse.ReadProductDetailResponse> productDetail =
                    productClient.readProductDetail(auction.getAuctionProductId());

            return productDetail.map(product -> ReadAuctionDetailResponse.of(auction, product));
        });
    }
}
