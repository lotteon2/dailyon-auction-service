package com.dailyon.auctionservice.common.webclient.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductResponse {
    private Long productId;
    private String imgPresignedUrl;
    private Map<String, String> describeImgPresignedUrl;
}
