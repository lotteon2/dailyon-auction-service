package com.dailyon.auctionservice.common.feign.client;

import com.dailyon.auctionservice.common.feign.response.CreateProductResponse;
import com.dailyon.auctionservice.config.FeignClientConfig;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "productFeignClient",
        url = "${endpoint.product-service}",
        configuration = FeignClientConfig.class
)
public interface ProductFeignClient {
    @PostMapping(value = "/client/products")
    ResponseEntity<CreateProductResponse> createAuctionProduct(@RequestBody CreateAuctionRequest.CreateProductRequest createProductRequest);
}
