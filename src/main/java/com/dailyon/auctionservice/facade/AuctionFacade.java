package com.dailyon.auctionservice.facade;

import com.dailyon.auctionservice.common.feign.client.ProductFeignClient;
import com.dailyon.auctionservice.common.feign.response.CreateProductResponse;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.dto.response.CreateAuctionResponse;
import com.dailyon.auctionservice.service.AuctionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionFacade {
    private final AuctionService auctionService;
    private final ProductFeignClient productFeignClient;

    public CreateAuctionResponse createAuction(CreateAuctionRequest createAuctionRequest) {
        CreateProductResponse productResponse;
        try {
            productResponse = productFeignClient.createAuctionProduct(createAuctionRequest.getProductRequest()).getBody();
        } catch (FeignException e) {
            throw new RuntimeException("경매 상품 등록에 실패했습니다");
        }
        Auction auction = auctionService.create(createAuctionRequest, productResponse);
        return CreateAuctionResponse.create(auction, productResponse);
    }
}
