package com.dailyon.auctionservice.chat;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionsManager {
  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
  private final Sinks.Many<WebSocketSession> sessionsSink =
      Sinks.many().multicast().onBackpressureBuffer();

  public void addSession(String userId, WebSocketSession session) {
    // 세션을 저장소에 추가
    sessions.put(userId, session);
    // Sink에 세션 추가
    sessionsSink.tryEmitNext(session);
  }

  public void removeSession(String userId) {
    // 세션을 저장소에서 제거
    sessions.remove(userId);
  }

  public Mono<Void> broadcast(String message) {
    return Flux.fromIterable(sessions.values())
            .flatMap(session -> session.send(Mono.just(session.textMessage(message))))
            .then(); // 모든 세션에 대한 send 작업이 완료될 때까지 기다림
  }
}
