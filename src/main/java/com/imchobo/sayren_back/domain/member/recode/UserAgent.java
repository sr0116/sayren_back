package com.imchobo.sayren_back.domain.member.recode;

import jakarta.validation.constraints.NotEmpty;

public record UserAgent(@NotEmpty(message = "아이피가 필요합니다.") String ip, @NotEmpty(message = "디바이스 정보가 필요합니다.") String device) {
}
