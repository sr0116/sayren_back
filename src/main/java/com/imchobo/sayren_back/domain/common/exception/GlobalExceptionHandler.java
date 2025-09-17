package com.imchobo.sayren_back.domain.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SayrenException.class)
  public ResponseEntity<Map<String, String>> handleSayrenException(SayrenException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST; // 기본값 400

    // 토큰 관련 오류는 401
    if ("TOKEN_EXPIRED".equals(ex.getErrorCode())){
      status = HttpStatus.UNAUTHORIZED;
    }

    return ResponseEntity
      .status(status)
      .body(Map.of(
        "errorCode", ex.getErrorCode(),
        "message", ex.getMessage()
      ));
  }
}
