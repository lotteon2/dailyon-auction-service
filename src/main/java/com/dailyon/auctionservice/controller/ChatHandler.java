package com.dailyon.auctionservice.controller;

import com.dailyon.auctionservice.chat.WebSocketSessionsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatHandler implements WebSocketHandler {
  private final WebSocketSessionsManager manager;

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    String query = session.getHandshakeInfo().getUri().getQuery();
    Map<String, String> queryMap = getQueryMap(query);
    String userId = queryMap.getOrDefault("id", "");
    System.out.println(queryMap);
    manager.addSession(userId, session);

    return session
        .receive()
        .flatMap(
            webSocketMessage -> {
              String payload = webSocketMessage.getPayloadAsText(StandardCharsets.UTF_8);
              try {
                return manager.broadcast(payload);
              } catch (Exception e) {
                e.printStackTrace();
                // 에러 처리 로직
                return Mono.empty();
              }
            })
        .then()
        .doFinally(signal -> manager.removeSession(userId)); // 사용자 연결 종료 시 세션 제거
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
}
