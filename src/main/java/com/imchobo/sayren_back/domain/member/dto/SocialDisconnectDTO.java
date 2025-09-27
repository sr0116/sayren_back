package com.imchobo.sayren_back.domain.member.dto;

import com.imchobo.sayren_back.domain.member.en.Provider;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialDisconnectDTO {
  @NotNull
  Provider provider;
}
