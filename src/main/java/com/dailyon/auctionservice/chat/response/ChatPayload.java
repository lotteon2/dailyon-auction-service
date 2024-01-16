package com.dailyon.auctionservice.chat.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatPayload<T> {
  private ChatCommand command;
  private T data;

  public static <T> ChatPayload<T> of(ChatCommand command, T data) {
    return (ChatPayload<T>) ChatPayload.<T>builder().command(command).data(data).build();
  }
}
