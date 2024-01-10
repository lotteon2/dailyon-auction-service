package com.dailyon.auctionservice.dto.request;

import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketSession;

@Data
public class UserSession {
  private Long userId;
  private WebSocketSession session;
}
