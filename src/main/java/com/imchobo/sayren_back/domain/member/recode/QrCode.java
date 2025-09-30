package com.imchobo.sayren_back.domain.member.recode;

import jakarta.validation.constraints.NotBlank;

public record QrCode(@NotBlank(message = "QR 코드 값은 비어있을 수 없습니다.") String qrCodeUrl) {
}
