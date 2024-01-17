package com.dailyon.auctionservice.chat.messaging;

import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.chat.util.ObjectStringConverter;
import com.dailyon.auctionservice.controller.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.dailyon.auctionservice.chat.util.ChatConstants.MESSAGE_TOPIC;

@Slf4j
@Component
@Profile({"!test"})
public class RedisChatMessageListener {

  private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
  private final ChatHandler chatWebSocketHandler;
  private final ObjectStringConverter objectStringConverter;

  public RedisChatMessageListener(
      @Qualifier("rsTemplate") ReactiveStringRedisTemplate reactiveStringRedisTemplate,
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
        .flatMap(payload -> objectStringConverter.stringToObject(payload, ChatPayload.class))
        .flatMap(chatWebSocketHandler::sendMessage)
        .then();
  }
}
