package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.chat.messaging.RedisChatMessagePublisher;
import com.dailyon.auctionservice.dto.request.Message;
import com.dailyon.auctionservice.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ChatHandler implements WebSocketHandler {
  private final Sinks.Many<Message> chatMessageSink;
  private final Flux<Message> chatMessageFluxSink;
  private final RedisChatMessagePublisher redisChatMessagePublisher;
  private final RedisAtomicLong activeUserCounter;
  private final ObjectStringConverter objectStringConverter;

  public ChatHandler(
      Sinks.Many<Message> chatMessageSink,
      RedisChatMessagePublisher redisChatMessagePublisher,
      RedisAtomicLong activeUserCounter,
      ObjectStringConverter objectStringConverter) {
    this.chatMessageSink = chatMessageSink;
    this.chatMessageFluxSink = chatMessageSink.asFlux();
    this.redisChatMessagePublisher = redisChatMessagePublisher;
    this.activeUserCounter = activeUserCounter;
    this.objectStringConverter = objectStringConverter;
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    String query = session.getHandshakeInfo().getUri().getQuery();
    Map<String, String> queryMap = getQueryMap(query);
    String userId = queryMap.getOrDefault("id", "");

    // 여기서 시작해봅니다...
    Flux<WebSocketMessage> sendMessageFlux =
        chatMessageFluxSink
            .flatMap(objectStringConverter::objectToString)
            .map(session::textMessage)
            .doOnError(
                throwable ->
                    log.error("Error Occurred while sending message to WebSocket.", throwable));

    Mono<Void> outputMessage = session.send(sendMessageFlux);
    //    manager.addSession(userId, session);

    Mono<Void> inputMessage =
        session
            .receive()
            .flatMap(
                webSocketMessage ->
                    redisChatMessagePublisher.publishChatMessage(
                        webSocketMessage.getPayloadAsText()))
            .doOnSubscribe(
                subscription -> {
                  long activeUserCount = activeUserCounter.incrementAndGet();
                  log.info(
                      "User '{}' Connected. Total Active Users: {}",
                      session.getId(),
                      activeUserCount);
                  chatMessageSink.tryEmitNext(
                      new Message("0", "CONNECTED", "CONNECTED", activeUserCount));
                })
            .doOnError(
                throwable -> log.error("Error Occurred while sending message to Redis.", throwable))
            .doFinally(
                signalType -> {
                  long activeUserCount = activeUserCounter.decrementAndGet();
                  log.info(
                      "User '{}' Disconnected. Total Active Users: {}",
                      session.getId(),
                      activeUserCount);
                  chatMessageSink.tryEmitNext(
                      new Message("0", "DISCONNECTED", "DISCONNECTED", activeUserCount));
                })
            .then();
    return Mono.zip(inputMessage, outputMessage).then();

  }

  private Map<String, String> getQueryMap(String queryStr) {
    Map<String, String> queryMap = new HashMap<>();
    if (!StringUtils.isEmpty(queryStr)) {
      String[] queryParam = queryStr.split("&");
      Arrays.stream(queryParam)
          .forEach(
              s -> {
                String[] kv = s.split("=", 2);
                String value = kv.length == 2 ? kv[1] : "";
                queryMap.put(kv[0], value);
              });
    }
    return queryMap;
  }

  public Mono<Sinks.EmitResult> sendMessage(Message chatMessage) {
    return Mono.fromSupplier(() -> chatMessageSink.tryEmitNext(chatMessage))
        .doOnSuccess(
            emitResult -> {
              if (emitResult.isFailure()) {
                log.error("Failed to send message with userId: {}", chatMessage.getUserId());
              }
            });
  }
}
