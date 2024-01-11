package com.dailyon.auctionservice.dto.request;

import lombok.*;

@Data
public class Message {
  private String userId;
  private String nickname;
  private String message;
  private String timestamp;
}