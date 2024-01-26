package com.dailyon.auctionservice.chat.messaging;

import com.dailyon.auctionservice.chat.response.ChatCommand;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.dailyon.auctionservice.chat.util.ChatConstants.MESSAGE_TOPIC;
import static com.dailyon.auctionservice.chat.util.ChatConstants.START_TOPIC;

@Slf4j
@Component
@Profile({"!test"})
@RequiredArgsConstructor
public class RedisChatMessagePublisher {

  @Qualifier("rsTemplate")
  private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

  private final ObjectMapper objectMapper;
  private String hostName;

  @PostConstruct
  public void init() {
    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      log.error("Error getting hostname.", e);
      hostName = "localhost";
    }
  }

  public Mono<Long> publishChatMessage(String message) {
    log.info("여기 publishChatMessage {}", message);
    return Mono.just(hostName)
        .map(
            host -> {
              try {
                ChatPayload chatPayload = objectMapper.readValue(message, ChatPayload.class);
                return objectMapper.writeValueAsString(chatPayload);
              } catch (JsonProcessingException e) {
                log.error("Error converting ChatMessage {} into string", message, e);
                return "EMPTY_MESSAGE";
              }
            })
        .flatMap(
            result -> {
              try {
                ChatPayload chatPayload = objectMapper.readValue(result, ChatPayload.class);
                if (chatPayload.getCommand().equals(ChatCommand.START)) {
                  return reactiveStringRedisTemplate
                      .convertAndSend(START_TOPIC, result)
                      .doOnSuccess(
                          aLong -> log.debug("Total of {} Messages published to Redis Topic."))
                      .doOnError(throwable -> log.error("Error publishing message.", throwable));
                }
              } catch (JsonProcessingException e) {
                e.printStackTrace();
              }
              // Publish Message to Redis Channels
              return reactiveStringRedisTemplate
                  .convertAndSend(MESSAGE_TOPIC, result)
                  .doOnSuccess(aLong -> log.debug("Total of {} Messages published to Redis Topic."))
                  .doOnError(throwable -> log.error("Error publishing message.", throwable));
            });
  }
}
