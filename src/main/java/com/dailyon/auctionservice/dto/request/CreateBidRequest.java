package com.dailyon.auctionservice.dto.request;

import com.dailyon.auctionservice.document.BidHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBidRequest {
  private String auctionId;
  private String round;
  private String nickname;
  private Long bidAmount;

  public BidHistory toEntity(String memberId) {
    BidHistory.PrimaryKey key = BidHistory.PrimaryKey.createKey(memberId);
    return BidHistory.builder()
        .primaryKey(key)
        .auctionId(auctionId)
        .nickname(nickname)
        .round(round)
        .bidAmount(bidAmount)
        .build();
  }
}
