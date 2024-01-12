package com.dailyon.auctionservice.chat.messaging;

import com.dailyon.auctionservice.dto.request.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.dailyon.auctionservice.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisChatMessagePublisher {

  private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
  private final RedisAtomicInteger chatMessageCounter;
  private final RedisAtomicLong activeUserCounter;
  private final ObjectMapper objectMapper;

  public Mono<Long> publishChatMessage(String message) {
    Integer totalChatMessage = chatMessageCounter.incrementAndGet();
    return Mono.fromCallable(
            () -> {
              try {
                return InetAddress.getLocalHost().getHostName();
              } catch (UnknownHostException e) {
                log.error("Error getting hostname.", e);
              }
              log.info("inetAddress.getLocalHost().getHostName() {} ", InetAddress.getLocalHost().getHostName());
              return "localhost";
            })
        .map(
            hostName -> {
              log.info("message -> {}", message);
              String chatString = "EMPTY_MESSAGE";
              try {
                Message chatMessage = objectMapper.readValue(message, Message.class);
                chatMessage.setActiveUserCount(activeUserCounter.get());
                chatString = objectMapper.writeValueAsString(chatMessage);
              } catch (JsonProcessingException e) {
                log.error("Error converting ChatMessage {} into string", message, e);
                log.error("Error converting ChatMessage {} into string", "chatMessage", e);
              }
              return chatString;
            })
        .flatMap(
            chatString -> {
              // Publish Message to Redis Channels
              return reactiveStringRedisTemplate
                  .convertAndSend(MESSAGE_TOPIC, chatString)
                  .doOnSuccess(
                      aLong ->
                          log.debug(
                              "Total of {} Messages published to Redis Topic.", totalChatMessage))
                  .doOnError(throwable -> log.error("Error publishing message.", throwable));
            });
  }
}
