package com.dailyon.auctionservice.chat.messaging;

import com.dailyon.auctionservice.chat.response.ChatCommand;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.dto.request.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.dailyon.auctionservice.chat.util.ChatConstants.MESSAGE_TOPIC;

@Slf4j
@Component
@Profile({"!test"})
@RequiredArgsConstructor
public class RedisChatMessagePublisher {

  @Qualifier("rsTemplate")
  private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

  private final ObjectMapper objectMapper;

  public Mono<Long> publishChatMessage(String message) {
    return Mono.fromCallable(
            () -> {
              try {
                return InetAddress.getLocalHost().getHostName();
              } catch (UnknownHostException e) {
                log.error("Error getting hostname.", e);
              }
              return "localhost";
            })
        .map(
            hostName -> {
              String result = "EMPTY_MESSAGE";
              try {
                ChatPayload chatPayload = objectMapper.readValue(message, ChatPayload.class);
                result = objectMapper.writeValueAsString(chatPayload);
              } catch (JsonProcessingException e) {
                log.error("Error converting ChatMessage {} into string", message, e);
                log.error("Error converting ChatMessage {} into string", "chatMessage", e);
              }
              return result;
            })
        .flatMap(
            result -> {
              // Publish Message to Redis Channels
              return reactiveStringRedisTemplate
                  .convertAndSend(MESSAGE_TOPIC, result)
                  .doOnSuccess(aLong -> log.debug("Total of {} Messages published to Redis Topic."))
                  .doOnError(throwable -> log.error("Error publishing message.", throwable));
            });
  }
}
