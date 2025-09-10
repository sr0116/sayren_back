package com.imchobo.sayren_back.domain.common.config;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
  // 공통 응답
  private boolean ok;
  private String message;
  private T data;

  public static <T> ApiResponse<T> ok(T d){
    return ApiResponse.<T>builder().ok(true).data(d).build();
  }
  public static <T> ApiResponse<T> fail(String m) {
    return ApiResponse.<T>builder().ok(false).message(m).build();
  }

}
