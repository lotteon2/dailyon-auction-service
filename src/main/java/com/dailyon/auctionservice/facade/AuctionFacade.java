package com.dailyon.auctionservice.facade;

import com.dailyon.auctionservice.common.feign.client.ProductFeignClient;
import com.dailyon.auctionservice.common.feign.response.CreateProductResponse;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.dto.response.CreateAuctionResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse;
import com.dailyon.auctionservice.service.AuctionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import static com.dailyon.auctionservice.dto.response.ReadAuctionPageResponse.ReadAuctionResponse;


import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionFacade {
    private final AuctionService auctionService;
    private final ProductFeignClient productFeignClient;

    public CreateAuctionResponse createAuction(CreateAuctionRequest createAuctionRequest) {
        CreateProductResponse productResponse = null;
        Auction auction = null;
        try {
            productResponse = productFeignClient.createAuctionProduct(createAuctionRequest.getProductRequest()).getBody();
            auction = auctionService.create(createAuctionRequest, productResponse);
        } catch (FeignException e) {
            throw new RuntimeException("경매 상품 등록에 실패했습니다");
        } catch (Exception e) { // 경매 정보 등록 실패 시 feign
            productFeignClient.deleteAuctionProduct(List.of(productResponse.getProductId()));
        }
        return CreateAuctionResponse.create(auction, productResponse);
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

    public ReadAuctionResponse readAuctionDetail(String auctionId) {
        return auctionService.readAuctionDetail(auctionId);
    }
}
