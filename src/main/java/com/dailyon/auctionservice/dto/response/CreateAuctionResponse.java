package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.common.feign.response.CreateProductResponse;
import com.dailyon.auctionservice.document.Auction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuctionResponse {
    private String auctionId;
    private Long productId;
    private String imgPresignedUrl;
    private Map<String, String> describeImgPresignedUrl;

    public static CreateAuctionResponse create(Auction auction, CreateProductResponse response) {
        return CreateAuctionResponse.builder()
                .auctionId(auction.getId())
                .productId(response.getProductId())
                .imgPresignedUrl(response.getImgPresignedUrl())
                .describeImgPresignedUrl(response.getDescribeImgPresignedUrl())
                .build();
    }
}
