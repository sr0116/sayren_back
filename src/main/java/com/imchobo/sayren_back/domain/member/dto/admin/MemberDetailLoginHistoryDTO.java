package com.imchobo.sayren_back.domain.member.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberDetailLoginHistoryDTO {
  String ip;
  String device;
  LocalDateTime regDate;
}
