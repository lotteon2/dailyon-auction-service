package com.dailyon.auctionservice.dto.response;

import com.dailyon.auctionservice.document.BidHistory;
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
}
