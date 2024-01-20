package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.chat.messaging.RedisChatMessagePublisher;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.chat.util.ObjectStringConverter;
import com.dailyon.auctionservice.dto.request.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static com.dailyon.auctionservice.chat.response.ChatCommand.MESSAGE;

@Slf4j
public class ChatHandler implements WebSocketHandler {
  private final Sinks.Many<ChatPayload> chatMessageSink;
  private final Flux<ChatPayload> chatMessageFluxSink;
  private final RedisChatMessagePublisher redisChatMessagePublisher;
  private final ObjectStringConverter objectStringConverter;

  public ChatHandler(
      Sinks.Many<ChatPayload> chatMessageSink,
      RedisChatMessagePublisher redisChatMessagePublisher,
      ObjectStringConverter objectStringConverter) {
    this.chatMessageSink = chatMessageSink;
    this.chatMessageFluxSink = chatMessageSink.asFlux();
    this.redisChatMessagePublisher = redisChatMessagePublisher;
    this.objectStringConverter = objectStringConverter;
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    Flux<WebSocketMessage> sendMessageFlux =
        chatMessageFluxSink
            .flatMap(objectStringConverter::objectToString)
            .map(session::textMessage)
            .doOnError(
                throwable ->
                    log.error("Error Occurred while sending message to WebSocket.", throwable));
    Mono<Void> outputMessage = session.send(sendMessageFlux);
    Mono<Void> inputMessage =
        session
            .receive()
            .flatMap(
                webSocketMessage ->
                    redisChatMessagePublisher.publishChatMessage(
                        webSocketMessage.getPayloadAsText()))
            .doOnSubscribe(
                subscription -> {
                  ChatPayload<Message> payload =
                      ChatPayload.of(MESSAGE, new Message("0", null, null));
                  chatMessageSink.tryEmitNext(payload);
                })
            .doOnError(
                throwable -> log.error("Error Occurred while sending message to Redis.", throwable))
            .doFinally(
                signalType -> {
                  ChatPayload<Message> payload =
                      ChatPayload.of(MESSAGE, new Message("0", null, null));
                  chatMessageSink.tryEmitNext(payload);
                })
            .then();
    return Mono.zip(inputMessage, outputMessage).then();
  }

  public Mono<Sinks.EmitResult> sendMessage(ChatPayload chatMessage) {
    return Mono.fromSupplier(() -> chatMessageSink.tryEmitNext(chatMessage))
        .doOnSuccess(
            emitResult -> {
              if (emitResult.isFailure()) {
                log.error("Failed to send message :  {}", chatMessage);
              }
            });
  }

  public Mono<Void> biddingBroadCast(ChatPayload chatPayload) {
    return objectStringConverter
        .objectToString(chatPayload)
        .flatMap(redisChatMessagePublisher::publishChatMessage)
        .then();
  }

  public Mono<Void> broadCast(ChatPayload chatPayload) {
    return objectStringConverter
        .objectToString(chatPayload)
        .flatMap(redisChatMessagePublisher::publishChatMessage)
        .then();
  }
}
