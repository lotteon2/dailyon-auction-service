package com.dailyon.auctionservice.chat.messaging;

import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.chat.scheduler.ChatScheduler;
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
import static com.dailyon.auctionservice.chat.util.ChatConstants.START_TOPIC;

@Slf4j
@Component
@Profile({"!test"})
public class RedisChatMessageListener {

  private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
  private final ChatHandler chatWebSocketHandler;
  private final ObjectStringConverter objectStringConverter;
  private final ChatScheduler scheduler;

  public RedisChatMessageListener(
      @Qualifier("rsTemplate") ReactiveStringRedisTemplate reactiveStringRedisTemplate,
      ChatHandler chatWebSocketHandler,
      ObjectStringConverter objectStringConverter,
      ChatScheduler scheduler) {
    this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    this.chatWebSocketHandler = chatWebSocketHandler;
    this.objectStringConverter = objectStringConverter;
    this.scheduler = scheduler;
  }

  public Mono<Void> subscribeMessageChannelAndPublishOnWebSocket() {
    return reactiveStringRedisTemplate
        .listenTo(new PatternTopic(MESSAGE_TOPIC))
        .map(ReactiveSubscription.Message::getMessage)
        .flatMap(payload -> objectStringConverter.stringToObject(payload, ChatPayload.class))
        .flatMap(chatWebSocketHandler::sendMessage)
        .then();
  }

  public Mono<Void> subscribeStartTrigger() {
    return reactiveStringRedisTemplate
        .listenTo(new PatternTopic(START_TOPIC))
        .map(ReactiveSubscription.Message::getMessage)
        .flatMap(payload -> objectStringConverter.stringToObject(payload, ChatPayload.class))
        .flatMap(
            chatPayload -> {
              Object data = chatPayload.getData();
              if (data instanceof String) {
                scheduler.startJob((String) data);
              }
              return chatWebSocketHandler.sendMessage(
                  chatPayload); // send message and return its Mono
            })
        .then();
  }
}
