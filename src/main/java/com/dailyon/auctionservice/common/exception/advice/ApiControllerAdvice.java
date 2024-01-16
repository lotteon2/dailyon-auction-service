package com.dailyon.auctionservice.common.exception.advice;

import com.dailyon.auctionservice.common.exception.CustomException;
import com.dailyon.auctionservice.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiControllerAdvice {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> customException(CustomException e) {
    HttpStatus statusCode = e.getStatusCode();

    ErrorResponse body =
        ErrorResponse.builder()
            .code(statusCode)
            .message(e.getMessage())
            .validation(e.getValidation())
            .build();

    return ResponseEntity.status(statusCode).body(body);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {
    ErrorResponse response =
        ErrorResponse.builder().code(HttpStatus.BAD_REQUEST).message("잘못된 요청입니다.").build();

    FieldError fieldError = e.getFieldErrors().get(0);
    response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
    return response;
  }
}
