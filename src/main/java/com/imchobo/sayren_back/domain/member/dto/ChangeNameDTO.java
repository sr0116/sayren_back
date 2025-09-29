package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ChangeNameDTO {
  @NotBlank(message = "이름은 필수 입력 값입니다")
  @Size(min = 2, message = "이름은 최소 두글자 이상이어야 합니다")
  private String name;
}
