package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.document.AuctionHistory;
import com.dailyon.auctionservice.document.BidHistory;
import com.dailyon.auctionservice.dto.response.ReadAuctionDetailResponse.ReadProductDetailResponse;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidInfo {
  private String memberId;
  private String nickname;
  private String auctionId;
  private String round;

  public static BidInfo from(BidHistory history) {
    return BidInfo.builder()
        .memberId(history.getMemberId())
        .nickname(history.getNickname())
        .auctionId(history.getAuctionId())
        .round(history.getRound())
        .build();
  }

  public AuctionHistory createAuctionHistory(
      Auction auction,
      ReadProductDetailResponse product,
      Long bidAmount,
      long auctionWinnerBid,
      boolean isWinner) {
    return AuctionHistory.builder()
        .memberId(memberId)
        .auctionId(auctionId)
        .auctionName(auction.getAuctionName())
        .auctionProductId(auction.getAuctionProductId())
        .auctionProductImg(product.getImgUrl())
        .auctionProductName(product.getName())
        .auctionProductSizeId(product.getProductStocks().get(0).getProductSizeId())
        .auctionProductSizeName(product.getProductStocks().get(0).getProductSizeName())
        .auctionWinnerBid(auctionWinnerBid)
        .isWinner(isWinner)
        .memberHighestBid(bidAmount)
        .build();
  }
}
