package com.dailyon.auctionservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopBidderResponse {
  private String memberId;
  private String nickname;
  private Long bidAmount;

  public static TopBidderResponse from(BidInfo bidInfo, Long bidAmount) {
    return TopBidderResponse.builder()
        .memberId(bidInfo.getMemberId())
        .nickname(bidInfo.getNickname())
        .bidAmount(bidAmount)
        .build();
  }
}
