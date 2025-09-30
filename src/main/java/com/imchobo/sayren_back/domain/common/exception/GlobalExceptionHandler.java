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
    HttpStatus status = switch (ex.getErrorCode()) {
      case "TOKEN_EXPIRED", "UNAUTHORIZED_ACCESS", "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED; // 401
      case "ACCESS_DENIED", "ROLE_NOT_ALLOWED" -> HttpStatus.FORBIDDEN; // 403
      case "EMAIL_ALREADY_EXISTS", "SOCIAL_LINK_FAILED", "ALREADY_LINKED_PROVIDER" -> HttpStatus.CONFLICT; // 409
      default -> HttpStatus.BAD_REQUEST; // 400
    };

    return ResponseEntity
      .status(status)
      .body(Map.of(
        "errorCode", ex.getErrorCode(),
        "message", ex.getMessage()
      ));
  }
}
