package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;
import com.imchobo.sayren_back.domain.member.en.Provider;
import jakarta.validation.constraints.NotNull;

public class AlreadyLinkedProviderException extends SayrenException {

  public AlreadyLinkedProviderException(@NotNull(message = "Provider 정보가 필요합니다") Provider providerName) {
    super(
      "ALREADY_LINKED_PROVIDER",
      "이미 연동된 소셜 계정입니다: " + providerName
    );
  }
}
