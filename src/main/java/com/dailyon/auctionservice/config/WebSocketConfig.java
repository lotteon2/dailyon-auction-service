package com.dailyon.auctionservice.config;

import com.dailyon.auctionservice.chat.messaging.RedisChatMessagePublisher;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.chat.util.ChatConstants;
import com.dailyon.auctionservice.chat.util.ObjectStringConverter;
import com.dailyon.auctionservice.controller.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Profile({"local", "prod"})
@Configuration(proxyBeanMethods = false)
public class WebSocketConfig {

  @Bean
  public ChatHandler webSocketHandler(
      RedisChatMessagePublisher redisChatMessagePublisher,
      ObjectStringConverter objectStringConverter) {
    Sinks.Many<ChatPayload> chatMessageSink = Sinks.many().multicast().directBestEffort();
    return new ChatHandler(chatMessageSink, redisChatMessagePublisher, objectStringConverter);
  }

  @Bean
  public HandlerMapping webSocketMapping(ChatHandler handler) {
    SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
    simpleUrlHandlerMapping.setCorsConfigurations(
        Collections.singletonMap("*", new CorsConfiguration().applyPermitDefaultValues()));
    Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();
    handlerMap.put(ChatConstants.WEBSOCKET_MESSAGE_MAPPING, handler);
    simpleUrlHandlerMapping.setUrlMap(handlerMap);
    simpleUrlHandlerMapping.setOrder(1);
    return simpleUrlHandlerMapping;
  }

  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
