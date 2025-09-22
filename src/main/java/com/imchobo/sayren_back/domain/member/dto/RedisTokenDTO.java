package com.imchobo.sayren_back.domain.member.dto;

import com.imchobo.sayren_back.domain.member.en.TokenStatus;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisTokenDTO {
  private String token;
  private TokenStatus tokenStatus;
}
