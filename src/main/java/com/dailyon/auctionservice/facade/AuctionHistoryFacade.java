package com.dailyon.auctionservice.facade;

import com.dailyon.auctionservice.common.webclient.client.ProductClient;
import com.dailyon.auctionservice.document.AuctionHistory;
import com.dailyon.auctionservice.dto.response.AuctionProductInfo;
import com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse;
import com.dailyon.auctionservice.dto.response.ReadAuctionHistoryPageResponse;
import com.dailyon.auctionservice.service.AuctionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuctionHistoryFacade {
  private final AuctionHistoryService auctionHistoryService;
  private final ProductClient productClient;

  public ReadAuctionHistoryPageResponse readAuctionHistories(String memberId, Pageable pageable) {
    return ReadAuctionHistoryPageResponse.of(
        auctionHistoryService.readAuctionHistories(memberId, pageable));
  }

  public Mono<AuctionProductInfo> readAuctionHistoryAndProductDetail(
      Long memberId, String auctionId) {
    AuctionHistory auctionHistory =
        auctionHistoryService.getAuctionHistory(String.valueOf(memberId), auctionId);
    Mono<ReadAuctionDetailResponse.ReadProductDetailResponse> readProductDetailResponseMono =
        productClient.readProductDetail(auctionHistory.getAuctionProductId());
    return readProductDetailResponseMono.flatMap(
        product -> {
          AuctionProductInfo auctionProductInfo =
              AuctionProductInfo.builder()
                  .productId(auctionHistory.getAuctionProductId())
                  .productName(product.getName())
                  .stock(product.getProductStocks().get(0).getQuantity().intValue())
                  .price(product.getPrice())
                  .gender(product.getGender())
                  .imgUrl(product.getImgUrl())
                  .sizeId(product.getProductStocks().get(0).getProductSizeId())
                  .sizeName(product.getProductStocks().get(0).getProductSizeName())
                  .isWinner(auctionHistory.isWinner())
                  .orderPrice(auctionHistory.getAuctionWinnerBid())
                  .categoryId(product.getCategoryId())
                  .build();
          return Mono.just(auctionProductInfo);
        });
  }
}
