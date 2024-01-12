package com.dailyon.auctionservice.chat.messaging;

import com.dailyon.auctionservice.controller.ChatHandler;
import com.dailyon.auctionservice.dto.request.Message;
import com.dailyon.auctionservice.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.dailyon.auctionservice.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
public class RedisChatMessageListener {

  private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
  private final ChatHandler chatWebSocketHandler;
  private final ObjectStringConverter objectStringConverter;

  public RedisChatMessageListener(
      ReactiveStringRedisTemplate reactiveStringRedisTemplate,
      ChatHandler chatWebSocketHandler,
      ObjectStringConverter objectStringConverter) {
    this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    this.chatWebSocketHandler = chatWebSocketHandler;
    this.objectStringConverter = objectStringConverter;
  }

  public Mono<Void> subscribeMessageChannelAndPublishOnWebSocket() {
    return reactiveStringRedisTemplate
        .listenTo(new PatternTopic(MESSAGE_TOPIC))
        .map(ReactiveSubscription.Message::getMessage)
        .flatMap(message -> objectStringConverter.stringToObject(message, Message.class))
        .filter(chatMessage -> !chatMessage.getMessage().isEmpty())
        .flatMap(chatWebSocketHandler::sendMessage)
        .then();
  }
}
