package com.dailyon.auctionservice.config;

import com.dailyon.auctionservice.controller.ChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
  @Bean
  public HandlerMapping webSocketMapping(ChatHandler handler) {
    SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
    Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();
    handlerMap.put("/ws/chat", handler);
    simpleUrlHandlerMapping.setUrlMap(handlerMap);
    simpleUrlHandlerMapping.setOrder(-1);
    return simpleUrlHandlerMapping;
  }

  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
