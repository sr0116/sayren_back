package com.imchobo.sayren_back.domain.common.config;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseEx<T> {
  // 공통 응답
  private boolean ok;
  private String message;
  private T data;

  public static <T> ApiResponseEx<T> ok(T d){
    return ApiResponseEx.<T>builder().ok(true).data(d).build();
  }
  public static <T> ApiResponseEx<T> fail(String m) {
    return ApiResponseEx.<T>builder().ok(false).message(m).build();
  }

}
