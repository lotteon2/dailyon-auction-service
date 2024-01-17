package com.dailyon.auctionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnterResponse {
  private String token;
  private ReadAuctionDetailResponse readAuctionDetailResponse;

  public static EnterResponse of(String token, ReadAuctionDetailResponse response) {
    return new EnterResponse(token, response);
  }
}
