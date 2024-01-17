package com.dailyon.auctionservice.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthorizationException extends CustomException {
  private static final String MESSAGE = "권한이 없습니다.";

  public AuthorizationException() {
    super(MESSAGE);
  }

  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.FORBIDDEN;
  }
}
