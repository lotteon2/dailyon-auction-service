package com.dailyon.auctionservice.common.webclient.client;


import com.dailyon.auctionservice.common.webclient.response.CreateProductResponse;
import com.dailyon.auctionservice.dto.request.CreateAuctionRequest;
import com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductClient {
    private final WebClient webClient;
    public Mono<ReadAuctionDetailResponse.ReadProductDetailResponse> readProductDetail(Long productId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/products/id/{productId}").build(productId))
                .retrieve()
                .bodyToMono(ReadAuctionDetailResponse.ReadProductDetailResponse.class);
    }

    public void deleteProducts(String memberId, String role, List<Long> ids) {
        String joinedIds = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/admin/products?ids={joinedIds}").build(joinedIds))
                .header("memberId", memberId)
                .header("role", role)
                .retrieve();
    }

    public Mono<CreateProductResponse> createProduct(String memberId, String role,
                                                     CreateAuctionRequest.CreateProductRequest request) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/admin/products").build())
                .header("memberId", memberId)
                .header("role", role)
                .body(Mono.just(request), CreateAuctionRequest.CreateProductRequest.class)
                .retrieve()
                .bodyToMono(CreateProductResponse.class);
    }
}
