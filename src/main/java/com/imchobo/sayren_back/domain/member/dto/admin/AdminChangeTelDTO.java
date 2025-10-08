package com.imchobo.sayren_back.domain.member.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AdminChangeTelDTO {
  @NotNull
  private Long memberId;

  @NotBlank(message = "전화번호는 필수 입력 값입니다")
  @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
  private String tel;
}
