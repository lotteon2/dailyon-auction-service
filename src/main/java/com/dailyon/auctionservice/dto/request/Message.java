package com.dailyon.auctionservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  private String userId;
  private String nickname;
  private String message;
  private long activeUserCount;
}
