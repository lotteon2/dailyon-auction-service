package com.dailyon.auctionservice.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamException extends CustomException {

  public static final String MESSAGE = "요청한 값이 올바르지 않습니다.";

  public InvalidParamException() {
    super(MESSAGE);
  }

  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }
}
