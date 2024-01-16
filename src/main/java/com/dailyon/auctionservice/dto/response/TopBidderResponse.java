package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.document.BidHistory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopBidderResponse {
  private String memberId;
  private String nickname;
  private Long bidAmount;

  public static TopBidderResponse from(BidHistory bidHistory) {
    return TopBidderResponse.builder()
        .memberId(bidHistory.getMemberId())
        .nickname(bidHistory.getNickname())
        .bidAmount(bidHistory.getBidAmount())
        .build();
  }
  ;
}
